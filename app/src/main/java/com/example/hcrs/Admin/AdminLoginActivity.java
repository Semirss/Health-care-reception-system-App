package com.example.hcrs.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hcrs.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etAdminName, etAdminPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    // Change this to your actual API URL
    private static final String LOGIN_URL = "http://10.0.2.2:7000/api/adminLogin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etAdminName = findViewById(R.id.etAdminName);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String name = etAdminName.getText().toString().trim();
        String password = etAdminPassword.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("password", password);

            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url(LOGIN_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        Toast.makeText(AdminLoginActivity.this, "Network Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                    });

                    if (response.isSuccessful()) {
                        // Read the response (optional: parse JSON response from your API)
                        String resp = "";
                        try {
                            resp = response.body().string();
                        } catch (Exception ignored) {}

                        runOnUiThread(() -> {
                            // You can customize based on your API response
                            Toast.makeText(AdminLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            // TODO: Navigate to admin dashboard or next screen
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(AdminLoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
