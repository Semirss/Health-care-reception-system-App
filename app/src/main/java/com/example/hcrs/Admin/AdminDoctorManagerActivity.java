package com.example.hcrs.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.R;
import com.example.hcrs.adapter.DoctorAdapter;
import com.example.hcrs.api.ApiService;
import com.example.hcrs.data.entities.Doctor;
import com.example.hcrs.network.RetrofitClient;
import com.example.hcrs.wrapper.DoctorResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AdminDoctorManagerActivity extends AppCompatActivity implements DoctorAdapter.OnItemClickListener {

    private RecyclerView rvDoctors;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admindoctor_manager);

        rvDoctors = findViewById(R.id.rvDoctors);

        adapter = new DoctorAdapter(this, doctorList);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        rvDoctors.setAdapter(adapter);

        loadDoctors();

        findViewById(R.id.fabAddDoctor).setOnClickListener(v -> showAddDoctorDialog());
    }

    private void showAddDoctorDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.admin_add_doctor_activity, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Doctor")
                .setView(dialogView)
                .setCancelable(true)
                .create();

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etAddress = dialogView.findViewById(R.id.etAddress);
        EditText etPhone = dialogView.findViewById(R.id.etPhone);
        EditText etSpecialization = dialogView.findViewById(R.id.etSpecialization);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            Doctor newDoctor = new Doctor(
                    etName.getText().toString().trim(),
                    etEmail.getText().toString().trim(),
                    etAddress.getText().toString().trim(),
                    etPhone.getText().toString().trim(),
                    etSpecialization.getText().toString().trim(),
                    etPassword.getText().toString().trim()
            );

            dialog.dismiss();
            addDoctor(newDoctor);
        });

        dialog.show();
    }

    private void loadDoctors() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<DoctorResponse> call = apiService.getAllDoctors();

        call.enqueue(new retrofit2.Callback<DoctorResponse>() {
            @Override
            public void onResponse(Call<DoctorResponse> call, Response<DoctorResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Doctor> doctorsFromApi = response.body().getData();
                    if (doctorsFromApi != null && !doctorsFromApi.isEmpty()) {
                        doctorList.clear();
                        doctorList.addAll(doctorsFromApi);
                        Log.d("LoadDoctors", "Doctors loaded: " + doctorsFromApi.toString());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminDoctorManagerActivity.this, "No doctors found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "No response body (HTTP " + response.code() + ")";
                    Toast.makeText(AdminDoctorManagerActivity.this, "Failed to load doctors: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("LoadDoctors", "Response code: " + response.code() + ", Message: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<DoctorResponse> call, Throwable t) {
                Toast.makeText(AdminDoctorManagerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoadDoctors", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void addDoctor(Doctor newDoctor) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<DoctorResponse> call = apiService.addDoctor(newDoctor);

        call.enqueue(new retrofit2.Callback<DoctorResponse>() {
            @Override
            public void onResponse(Call<DoctorResponse> call, Response<DoctorResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminDoctorManagerActivity.this, "Doctor added", Toast.LENGTH_SHORT).show();
                    loadDoctors();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "No response body (HTTP " + response.code() + ")";
                    Toast.makeText(AdminDoctorManagerActivity.this, "Failed to add doctor: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("AddDoctor", "Response code: " + response.code() + ", Message: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<DoctorResponse> call, Throwable t) {
                Toast.makeText(AdminDoctorManagerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AddDoctor", "Network error: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onEditClick(Doctor doctor) {
        showUpdateDoctorDialog(doctor);
    }

    @Override
    public void onDeleteClick(Doctor doctor) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Doctor")
                .setMessage("Are you sure you want to delete " + doctor.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteDoctor(doctor))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteDoctor(Doctor doctor) {
        Log.d("DeleteDoctor", "Attempting to delete doctor with ID: " + doctor.getId());
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<DoctorResponse> call = apiService.deleteDoctor(doctor.getId());

        call.enqueue(new retrofit2.Callback<DoctorResponse>() {
            @Override
            public void onResponse(Call<DoctorResponse> call, Response<DoctorResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminDoctorManagerActivity.this, "Doctor deleted successfully", Toast.LENGTH_SHORT).show();
                    loadDoctors();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "No response body (HTTP " + response.code() + ")";
                    if (response.code() == 404) {
                        errorMessage = "Doctor not found or endpoint incorrect";
                    }
                    Toast.makeText(AdminDoctorManagerActivity.this, "Failed to delete doctor: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("DeleteDoctor", "Response code: " + response.code() + ", Message: " + errorMessage + ", Raw response: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<DoctorResponse> call, Throwable t) {
                Toast.makeText(AdminDoctorManagerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("DeleteDoctor", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void showUpdateDoctorDialog(Doctor doctor) {
        View dialogView = getLayoutInflater().inflate(R.layout.admin_update_doctor_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Update Doctor")
                .setView(dialogView)
                .setCancelable(true)
                .create();

        EditText etName = dialogView.findViewById(R.id.etUpdateName);
        EditText etEmail = dialogView.findViewById(R.id.etUpdateEmail);
        EditText etAddress = dialogView.findViewById(R.id.etUpdateAddress);
        EditText etPhone = dialogView.findViewById(R.id.etUpdatePhone);
        EditText etSpecialization = dialogView.findViewById(R.id.etUpdateSpecialization);
        EditText etPassword = dialogView.findViewById(R.id.etUpdatePassword);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdateDoctor);

        etName.setText(doctor.getName());
        etEmail.setText(doctor.getEmail());
        etAddress.setText(doctor.getAddress());
        etPhone.setText(doctor.getPhoneNumber());
        etSpecialization.setText(doctor.getSpecialization());
        etPassword.setText(doctor.getPassword());

        btnUpdate.setOnClickListener(v -> {
            Doctor updatedDoctor = new Doctor(
                    etName.getText().toString().trim(),
                    etEmail.getText().toString().trim(),
                    etAddress.getText().toString().trim(),
                    etPhone.getText().toString().trim(),
                    etSpecialization.getText().toString().trim(),
                    etPassword.getText().toString().trim()
            );
            updatedDoctor.setId(doctor.getId());

            dialog.dismiss();
            updateDoctor(updatedDoctor);
        });

        dialog.show();
    }

    private void updateDoctor(Doctor updatedDoctor) {
        Log.d("UpdateDoctor", "Attempting to update doctor with ID: " + updatedDoctor.getId() + ", Doctor: " + updatedDoctor.toString());
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<DoctorResponse> call = apiService.updateDoctor(updatedDoctor.getId(), updatedDoctor);

        call.enqueue(new retrofit2.Callback<DoctorResponse>() {
            @Override
            public void onResponse(Call<DoctorResponse> call, Response<DoctorResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminDoctorManagerActivity.this, "Doctor updated successfully", Toast.LENGTH_SHORT).show();
                    loadDoctors();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "No response body (HTTP " + response.code() + ")";
                    if (response.code() == 404) {
                        errorMessage = "Doctor not found or endpoint incorrect";
                    }
                    Toast.makeText(AdminDoctorManagerActivity.this, "Failed to update doctor: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("UpdateDoctor", "Response code: " + response.code() + ", Message: " + errorMessage + ", Raw response: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<DoctorResponse> call, Throwable t) {
                Toast.makeText(AdminDoctorManagerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("UpdateDoctor", "Network error: " + t.getMessage(), t);
            }
        });
    }
}