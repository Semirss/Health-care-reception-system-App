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
import com.example.hcrs.adapter.ReceptionAdapter;
import com.example.hcrs.api.ApiService;
import com.example.hcrs.data.entities.Receptionist;
import com.example.hcrs.network.RetrofitClient;
import com.example.hcrs.wrapper.ResponseWrapper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AdminReceptionManagerActivity extends AppCompatActivity implements ReceptionAdapter.OnReceptionistClickListener {

    private RecyclerView recyclerView;
    private ReceptionAdapter adapter;
    private List<Receptionist> receptionistList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reception_manager);

        recyclerView = findViewById(R.id.recyclerReception);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReceptionAdapter(receptionistList, this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAddReception).setOnClickListener(v -> showAddEditDialog(null));

        loadReceptionists();
    }

    private void loadReceptionists() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseWrapper<Receptionist>> call = apiService.getAllReceptionists();

        call.enqueue(new retrofit2.Callback<ResponseWrapper<Receptionist>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Receptionist>> call, Response<ResponseWrapper<Receptionist>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Receptionist> receptionistsFromApi = response.body().getData();
                    if (receptionistsFromApi != null && !receptionistsFromApi.isEmpty()) {
                        receptionistList.clear();
                        receptionistList.addAll(receptionistsFromApi);
                        Log.d("LoadReceptionists", "Receptionists loaded: " + receptionistList.toString());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminReceptionManagerActivity.this, "No receptionists found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "No response body (HTTP " + response.code() + ")";
                    Toast.makeText(AdminReceptionManagerActivity.this, "Failed to load receptionists: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("LoadReceptionists", "Response code: " + response.code() + ", Message: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Receptionist>> call, Throwable t) {
                Toast.makeText(AdminReceptionManagerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoadReceptionists", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void showAddEditDialog(Receptionist receptionist) {
        View dialogView = getLayoutInflater().inflate(R.layout.admin_add_edit_receptionist_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(receptionist == null ? "Add New Receptionist" : "Edit Receptionist")
                .setView(dialogView)
                .setCancelable(true)
                .create();

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etAddress = dialogView.findViewById(R.id.etAddress);
        EditText etPhone = dialogView.findViewById(R.id.etPhone);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        if (receptionist != null) {
            etName.setText(receptionist.getName());
            etEmail.setText(receptionist.getEmail());
            etAddress.setText(receptionist.getAddress());
            etPhone.setText(receptionist.getPhone());
            etPassword.setText(receptionist.getPassword());
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            Receptionist newReceptionist = new Receptionist(
                    receptionist != null ? receptionist.getId() : 0,
                    name,
                    email,
                    address,
                    phone,
                    password
            );

            Log.d("ReceptionistDialog", "Receptionist: " + newReceptionist.toString());
            dialog.dismiss();
            if (receptionist == null) {
                addReceptionist(newReceptionist);
            } else {
                updateReceptionist(newReceptionist);
            }
        });

        dialog.show();
    }

    private void addReceptionist(Receptionist newReceptionist) {
        Log.d("AddReceptionist", "Adding receptionist: " + newReceptionist.toString());
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseWrapper<Receptionist>> call = apiService.addReceptionist(newReceptionist);

        call.enqueue(new retrofit2.Callback<ResponseWrapper<Receptionist>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Receptionist>> call, Response<ResponseWrapper<Receptionist>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminReceptionManagerActivity.this, "Receptionist added", Toast.LENGTH_SHORT).show();
                    loadReceptionists();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "No response body (HTTP " + response.code() + ")";
                    Toast.makeText(AdminReceptionManagerActivity.this, "Failed to add receptionist: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("AddReceptionist", "Response code: " + response.code() + ", Message: " + errorMessage + ", Raw response: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Receptionist>> call, Throwable t) {
                Toast.makeText(AdminReceptionManagerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("AddReceptionist", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void updateReceptionist(Receptionist updatedReceptionist) {
        Log.d("UpdateReceptionist", "Updating receptionist with ID: " + updatedReceptionist.getId());
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseWrapper<Receptionist>> call = apiService.updateReceptionist(updatedReceptionist.getId(), updatedReceptionist);

        call.enqueue(new retrofit2.Callback<ResponseWrapper<Receptionist>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Receptionist>> call, Response<ResponseWrapper<Receptionist>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminReceptionManagerActivity.this, "Receptionist updated successfully", Toast.LENGTH_SHORT).show();
                    loadReceptionists();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "No response body (HTTP " + response.code() + ")";
                    if (response.code() == 404) {
                        errorMessage = "Receptionist not found or endpoint incorrect";
                    }
                    Toast.makeText(AdminReceptionManagerActivity.this, "Failed to update receptionist: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("UpdateReceptionist", "Response code: " + response.code() + ", Message: " + errorMessage + ", Raw response: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Receptionist>> call, Throwable t) {
                Toast.makeText(AdminReceptionManagerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("UpdateReceptionist", "Network error: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onEditClick(Receptionist receptionist) {
        showAddEditDialog(receptionist);
    }

    @Override
    public void onDeleteClick(Receptionist receptionist) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Receptionist")
                .setMessage("Are you sure you want to delete " + receptionist.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteReceptionist(receptionist))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReceptionist(Receptionist receptionist) {
        Log.d("DeleteReceptionist", "Deleting receptionist with ID: " + receptionist.getId());
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseWrapper<Receptionist>> call = apiService.deleteReceptionist(receptionist.getId());

        call.enqueue(new retrofit2.Callback<ResponseWrapper<Receptionist>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Receptionist>> call, Response<ResponseWrapper<Receptionist>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminReceptionManagerActivity.this, "Receptionist deleted successfully", Toast.LENGTH_SHORT).show();
                    loadReceptionists();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "No response body (HTTP " + response.code() + ")";
                    if (response.code() == 404) {
                        errorMessage = "Receptionist not found or endpoint incorrect";
                    }
                    Toast.makeText(AdminReceptionManagerActivity.this, "Failed to delete receptionist: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("DeleteReceptionist", "Response code: " + response.code() + ", Message: " + errorMessage + ", Raw response: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Receptionist>> call, Throwable t) {
                Toast.makeText(AdminReceptionManagerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("DeleteReceptionist", "Network error: " + t.getMessage(), t);
            }
        });
    }
}