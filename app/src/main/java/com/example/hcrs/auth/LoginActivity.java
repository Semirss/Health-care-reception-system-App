package com.example.hcrs.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hcrs.Admin.AdminReceptionManagerActivity;
import com.example.hcrs.DoctorQueueManagerActivity;
import com.example.hcrs.PatientListActivity;
import com.example.hcrs.PatientPageActivity;
import com.example.hcrs.R;
import com.example.hcrs.api.ApiService;
import com.example.hcrs.data.entities.User;
import com.example.hcrs.network.RetrofitClient;
import com.example.hcrs.wrapper.loginresponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etName, etPassword;
    private Button btnLogin;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.login_page);
        } catch (Exception e) {
            Log.e("LoginActivity", "Error inflating layout: " + e.getMessage(), e);
            Toast.makeText(this, "Layout error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences hcrsPrefs = getSharedPreferences("hcrs_prefs", MODE_PRIVATE);
        boolean isLoggedOut = getIntent().getBooleanExtra("is_logged_out", false) || hcrsPrefs.getBoolean("is_logged_out", false);

        // Log SharedPreferences values for debugging
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.getString("role", "");
        int personId = prefs.getInt("person_id", 0);
        int doctorId = prefs.getInt("doctor_id", 0);
        Log.d("LoginActivity", "SharedPreferences: isLoggedIn=" + isLoggedIn + ", role=" + role + ", person_id=" + personId + ", doctor_id=" + doctorId);

        // Check if already logged in and not explicitly logged out
        if (!isLoggedOut && isLoggedIn) {
            if ("doctor".equalsIgnoreCase(role)) {
                if (doctorId == 0) {
                    Log.e("LoginActivity", "Invalid doctor_id, staying on login");
                    Toast.makeText(this, "Invalid doctor ID, please log in again", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(LoginActivity.this, DoctorQueueManagerActivity.class);
                intent.putExtra("doctor_id", doctorId);
                intent.putExtra("name", prefs.getString("name", ""));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if ("receptionist".equalsIgnoreCase(role)) {
                Intent intent = new Intent(LoginActivity.this, PatientListActivity.class);
                intent.putExtra("person_id", personId);
                intent.putExtra("name", prefs.getString("name", ""));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if ("patient".equalsIgnoreCase(role)) {
                int patientId = prefs.getInt("patient_id", 0);
                if (patientId == 0) {
                    Log.e("LoginActivity", "Invalid patient_id, staying on login");
                    Toast.makeText(this, "Invalid patient ID, please log in again", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(LoginActivity.this, PatientPageActivity.class);
                intent.putExtra("patient_id", patientId);
                intent.putExtra("name", prefs.getString("name", ""));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Log.e("LoginActivity", "Invalid role: " + role + ", clearing preferences");
                prefs.edit().clear().apply();
                Toast.makeText(this, "Invalid role, please log in again", Toast.LENGTH_LONG).show();
            }
            return;
        }

        // Initialize views
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        if (etName == null || etPassword == null || btnLogin == null) {
            Log.e("LoginActivity", "View initialization failed");
            Toast.makeText(this, "View initialization error", Toast.LENGTH_LONG).show();
            return;
        }

        btnLogin.setOnClickListener(v -> {
            Log.d("LoginActivity", "Login button clicked");
            Toast.makeText(this, "Login button clicked", Toast.LENGTH_SHORT).show();
            login();
        });
    }

    private void login() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        Log.d("LoginActivity", "Attempting login with name: " + name);

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Name and password are required", Toast.LENGTH_SHORT).show();
            Log.w("LoginActivity", "Empty name or password");
            return;
        }

        LoginRequest loginRequest = new LoginRequest(name, password);
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<loginresponse<User>> call = apiService.login(loginRequest);

        call.enqueue(new Callback<loginresponse<User>>() {
            @Override
            public void onResponse(Call<loginresponse<User>> call, Response<loginresponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    Log.d("LoginActivity", "Login success: role=" + user.getRole() + ", person_id=" + user.getPersonId() + ", patient_id=" + user.getPatientId());

                    if ("doctor".equalsIgnoreCase(user.getRole())) {
                        fetchDoctorId(user.getName(), user.getRole(), user);
                    } else if ("receptionist".equalsIgnoreCase(user.getRole())) {
                        saveLoginState(user.getPersonId(), user.getName(), user.getRole(), 0, null);
                        Intent intent = new Intent(LoginActivity.this, PatientListActivity.class);
                        intent.putExtra("person_id", user.getPersonId());
                        intent.putExtra("name", user.getName());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else if ("patient".equalsIgnoreCase(user.getRole())) {
                        Integer patientId = user.getPatientId();
                        if (patientId == null || patientId == 0) {
                            Toast.makeText(LoginActivity.this, "Invalid patient ID for patient role", Toast.LENGTH_LONG).show();
                            Log.e("LoginActivity", "Invalid patient_id: " + patientId);
                            return;
                        }
                        saveLoginState(user.getPersonId(), user.getName(), user.getRole(), 0, patientId);
                        Intent intent = new Intent(LoginActivity.this, PatientPageActivity.class);
                        intent.putExtra("patient_id", patientId);
                        intent.putExtra("name", user.getName());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Log.d("LoginActivity", "Navigating to PatientPageActivity with patient_id: " + patientId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Unknown role: " + user.getRole(), Toast.LENGTH_SHORT).show();
                        Log.w("LoginActivity", "Unknown role: " + user.getRole());
                    }
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Error: HTTP " + response.code();
                    Toast.makeText(LoginActivity.this, "Invalid password or username: " + message, Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", "Login failed: " + message + ", Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<loginresponse<User>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void fetchDoctorId(String name, String role, User user) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<loginresponse<Integer>> call = apiService.getDoctorId(name, role);

        call.enqueue(new Callback<loginresponse<Integer>>() {
            @Override
            public void onResponse(Call<loginresponse<Integer>> call, Response<loginresponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    int doctorId = response.body().getData();
                    Log.d("LoginActivity", "Doctor ID fetched: " + doctorId);
                    saveLoginState(user.getPersonId(), user.getName(), user.getRole(), doctorId, null);
                    Intent intent = new Intent(LoginActivity.this, DoctorQueueManagerActivity.class);
                    intent.putExtra("doctor_id", doctorId);
                    intent.putExtra("name", user.getName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Error: HTTP " + response.code();
                    Toast.makeText(LoginActivity.this, "Failed to fetch doctor ID: " + message, Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", "Fetch doctor ID failed: " + message);
                }
            }

            @Override
            public void onFailure(Call<loginresponse<Integer>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void saveLoginState(int personId, String name, String role, int doctorId, Integer patientId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putInt("person_id", personId);
        editor.putString("name", name);
        editor.putString("role", role);
        if ("doctor".equalsIgnoreCase(role)) {
            editor.putInt("doctor_id", doctorId);
        }
        if ("patient".equalsIgnoreCase(role) && patientId != null) {
            editor.putInt("patient_id", patientId);
        }
        editor.apply();

        // Clear is_logged_out in hcrs_prefs
        SharedPreferences hcrsPrefs = getSharedPreferences("hcrs_prefs", MODE_PRIVATE);
        hcrsPrefs.edit().putBoolean("is_logged_out", false).apply();

        Log.d("LoginActivity", "Saved login state: person_id=" + personId + ", role=" + role + ", doctor_id=" + doctorId + ", patient_id=" + patientId);
    }
}