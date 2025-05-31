package com.example.hcrs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.adapter.QueueAdapter;
import com.example.hcrs.auth.LoginActivity;
import com.example.hcrs.data.entities.Que;
import com.example.hcrs.network.RetrofitClient;
import com.example.hcrs.api.ApiService;
import com.example.hcrs.wrapper.loginresponse;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorQueueManagerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private QueueAdapter queueAdapter;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_page);

        recyclerView = findViewById(R.id.appointmentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        queueAdapter = new QueueAdapter(this);
        recyclerView.setAdapter(queueAdapter);

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        int doctorId = getIntent().getIntExtra("doctor_id", -1);
        if (doctorId == -1) {
            Log.e("DoctorQueueManager", "Doctor ID not provided in intent");
            Toast.makeText(this, "Doctor ID not provided", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d("DoctorQueueManager", "Received doctorID for queue: " + doctorId);
        fetchQueueByDoctorID(doctorId);
    }

    private void fetchQueueByDoctorID(int doctorId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<loginresponse<Que[]>> call = apiService.getQueueByDoctorID(doctorId);

        call.enqueue(new Callback<loginresponse<Que[]>>() {
            @Override
            public void onResponse(Call<loginresponse<Que[]>> call, Response<loginresponse<Que[]>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Que[] queues = response.body().getData();
                    if (queues != null && queues.length > 0) {
                        for (Que queue : queues) {
                            Log.d("DoctorQueueManager", "Queue item: status=" + queue.getStatus() +
                                    ", isStatus=" + queue.isStatus() +
                                    ", cardId=" + queue.getCardId() +
                                    ", date=" + queue.getDate());
                        }
                        queueAdapter.setQueueList(Arrays.asList(queues));
                    } else {
                        Log.w("DoctorQueueManager", "Queue data is null or empty");
                        Toast.makeText(DoctorQueueManagerActivity.this, "No queue data available", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Error: HTTP " + response.code();
                    Log.e("DoctorQueueManager", "Response failed: " + response.code() + ", Message: " + message + ", Raw: " + response.raw());
                    Toast.makeText(DoctorQueueManagerActivity.this, "Failed to fetch queue: " + message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<loginresponse<Que[]>> call, Throwable t) {
                Log.e("DoctorQueueManager", "Network error: " + t.getMessage(), t);
                Toast.makeText(DoctorQueueManagerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(DoctorQueueManagerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}