package com.example.hcrs.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hcrs.R;
import com.example.hcrs.api.ApiService;
import com.example.hcrs.auth.LoginActivity;
import com.example.hcrs.data.entities.Patient;
import com.example.hcrs.network.RetrofitInstance;
import com.example.hcrs.wrapper.ApiResponse;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText fullNameInput, emailInput, addressInput, phoneNumberInput, passwordInput;
    private Button registerButton;
    private TextView loginLink;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.register_page);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout: ", e);
            Toast.makeText(this, "Layout error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Initialize views
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        addressInput = findViewById(R.id.addressInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        if (fullNameInput == null || emailInput == null || addressInput == null ||
                phoneNumberInput == null || passwordInput == null || registerButton == null || loginLink == null) {
            Log.e(TAG, "View initialization failed");
            Toast.makeText(this, "UI error: Check register_page.xml", Toast.LENGTH_LONG).show();
            return;
        }

        // Initialize API service
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        // Setup register button
        registerButton.setOnClickListener(v -> {
            String name = fullNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate inputs
            if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Name, email, phone, and password are required", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Empty input fields: name=" + name + ", email=" + email + ", address=" + address + ", phoneNumber=" + phoneNumber + ", password=" + (password.isEmpty() ? "empty" : "set"));
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Invalid email: " + email);
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Password too short");
                return;
            }

            // Create Patient object
            Patient patient = new Patient();
            patient.setName(name);
            patient.setEmail(email);
            patient.setAddress(address);
            patient.setPhoneNumber(phoneNumber);
            patient.setPassword(password);
            patient.setRole("patient");
            patient.setHistory(new HashMap<>());
            patient.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

            // Log request payload
            String jsonPayload = new Gson().toJson(patient);
            Log.d(TAG, "Registering patient: " + jsonPayload);

            // Call API
            apiService.registerPatient(patient).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Void> apiResponse = response.body();
                        Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Registration response: " + apiResponse.getMessage());
                        if (apiResponse.isSuccess()) {
                            setResult(RESULT_OK); // Signal successful registration
                            finish(); // Return to PatientListActivity
                        }
                    } else {
                        String message = response.body() != null && response.body().getMessage() != null ? response.body().getMessage() : "HTTP " + response.code();
                        Toast.makeText(RegisterActivity.this, "Failed to register: " + message, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Registration failed: " + message + ", Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Registration network error: ", t);
                }
            });
        });

        // Setup login link
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            Log.d(TAG, "Navigating to LoginActivity");
        });
    }
}