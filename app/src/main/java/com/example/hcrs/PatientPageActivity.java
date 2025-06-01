package com.example.hcrs;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.adapter.PatientQueueAdapter;
import com.example.hcrs.api.ApiService;
import com.example.hcrs.auth.LoginActivity;
import com.example.hcrs.data.entities.Que;
import com.example.hcrs.network.RetrofitClient;
import com.example.hcrs.wrapper.loginresponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientPageActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private PatientQueueAdapter queueAdapter;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_page);

        // Initialize SharedPreferences
        prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Log SharedPreferences
        int storedPersonId = prefs.getInt("person_id", 0);
        int storedPatientId = prefs.getInt("patient_id", 0);
        String storedRole = prefs.getString("role", "");
        String storedName = prefs.getString("name", "");
        Log.d("PatientPageActivity", "SharedPreferences: person_id=" + storedPersonId + ", patient_id=" + storedPatientId + ", role=" + storedRole + ", name=" + storedName);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.queueRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        queueAdapter = new PatientQueueAdapter(this);
        recyclerView.setAdapter(queueAdapter);

        // Initialize API service
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Initialize logout button
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        // Request POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }

        // Get patient ID from Intent
        int patientId = getIntent().getIntExtra("patient_id", 0);
        Log.d("PatientPageActivity", "Intent extras: patient_id=" + patientId + ", name=" + getIntent().getStringExtra("name"));
        if (patientId == 0) {
            Toast.makeText(this, "Invalid patient ID", Toast.LENGTH_SHORT).show();
            Log.e("PatientPageActivity", "No patient_id provided in Intent");
            finish();
            return;
        }

        // Fetch queue data
        fetchQueueData(patientId);
    }

    private void fetchQueueData(int patientId) {
        Log.d("PatientPageActivity", "Fetching queue for patient_id: " + patientId);
        Call<loginresponse<List<Que>>> call = apiService.getQueueByPatientID(patientId);
        String apiUrl = RetrofitClient.getRetrofitInstance().baseUrl().toString() + "queue/patient/" + patientId;
        Log.d("PatientPageActivity", "API URL: " + apiUrl);

        call.enqueue(new Callback<loginresponse<List<Que>>>() {
            @Override
            public void onResponse(Call<loginresponse<List<Que>>> call, Response<loginresponse<List<Que>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PatientPageActivity", "Raw response: " + response.body().toString());
                    Log.d("PatientPageActivity", "Raw data: " + response.body().getRawDataAsString());

                    if (response.body().isSuccess()) {
                        List<Que> queueList = response.body().getData();
                        if (queueList != null && !queueList.isEmpty()) {
                            queueAdapter.setQueueList(queueList);
                            Log.d("PatientPageActivity", "Queue data fetched: " + queueList.size() + " items");
                            Toast.makeText(PatientPageActivity.this, "Queue loaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PatientPageActivity.this, "No queue items found for patient ID " + patientId, Toast.LENGTH_SHORT).show();
                            queueAdapter.setQueueList(new ArrayList<>());
                            Log.w("PatientPageActivity", "Queue data is empty or null for patient_id: " + patientId);
                        }
                    } else {
                        String errorMsg = response.body().getMessage();
                        Toast.makeText(PatientPageActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("PatientPageActivity", "API error: " + errorMsg);
                        queueAdapter.setQueueList(new ArrayList<>());
                    }
                } else {
                    String errorMsg = "HTTP error: " + response.code() + " - " + response.message() + ". Check if patient ID " + patientId + " is valid.";
                    Toast.makeText(PatientPageActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("PatientPageActivity", errorMsg);
                    queueAdapter.setQueueList(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<loginresponse<List<Que>>> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage() + ". Ensure server is running at " + apiUrl;
                Toast.makeText(PatientPageActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e("PatientPageActivity", "Network error", t);
                queueAdapter.setQueueList(new ArrayList<>());
            }
        });
    }

    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Log.d("PatientPageActivity", "Logged out: SharedPreferences cleared");

        Intent intent = new Intent(PatientPageActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications disabled without permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}