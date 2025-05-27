package com.example.hcrs.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hcrs.Admin.AdminDoctorManagerActivity;
import com.example.hcrs.Admin.AdminReceptionManagerActivity;
import com.example.hcrs.R;
import com.example.hcrs.api.ApiService;
import com.example.hcrs.auth.LoginRequest;
import com.example.hcrs.data.entities.User;
import com.example.hcrs.network.RetrofitClient;
import com.example.hcrs.wrapper.loginresponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etName, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Name and password are required", Toast.LENGTH_SHORT).show();
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
                    Log.d("LoginActivity", "Login success: " + user.getRole());
                    Intent intent;
                    if ("doctor".equalsIgnoreCase(user.getRole())) {
                        intent = new Intent(LoginActivity.this, AdminDoctorManagerActivity.class);
                    } else if ("receptionist".equalsIgnoreCase(user.getRole())) {
                        intent = new Intent(LoginActivity.this, AdminReceptionManagerActivity.class);
                    } else {
                        Toast.makeText(LoginActivity.this, "Unknown role", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.putExtra("person_id", user.getPersonId());
                    intent.putExtra("name", user.getName());
                    startActivity(intent);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Error: HTTP " + response.code();
                    Toast.makeText(LoginActivity.this, "invaild password or username"+message, Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", "Error: " + message + ", Code: " + response.code());
                }
            }


            @Override
            public void onFailure(Call<loginresponse<User>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Network error: " + t.getMessage(), t);
            }
        });
    }
}