package com.example.hcrs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.adapter.PatientAdapter;
import com.example.hcrs.adapter.RegisterActivity;
import com.example.hcrs.api.ApiService;
import com.example.hcrs.auth.LoginActivity;
import com.example.hcrs.data.entities.DoctorSelection;
import com.example.hcrs.data.entities.Patient;
import com.example.hcrs.network.RetrofitClient;
import com.example.hcrs.wrapper.ApiResponse;
import com.example.hcrs.wrapper.AppointmentRequest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientListActivity extends AppCompatActivity implements PatientAdapter.OnPatientActionListener {
    private static final String TAG = "PatientListActivity";
    private static final int REQUEST_CODE_REGISTER = 1;
    private RecyclerView recyclerView;
    private PatientAdapter patientAdapter;
    private List<Patient> patientList;
    private ApiService apiService;
    private List<DoctorSelection> doctorList;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reseptionistpage);

        prefs = getSharedPreferences("hcrs_prefs", MODE_PRIVATE);

        recyclerView = findViewById(R.id.patientRecyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            patientList = new ArrayList<>();
            patientAdapter = new PatientAdapter(patientList, this);
            recyclerView.setAdapter(patientAdapter);
        } else {
            Log.e(TAG, "RecyclerView not found in layout");
            Toast.makeText(this, "UI error: RecyclerView not found", Toast.LENGTH_LONG).show();
            return;
        }

        ImageView logoutIcon = findViewById(R.id.logoutIcon);
        if (logoutIcon != null) {
            logoutIcon.setOnClickListener(v -> logout());
        } else {
            Log.e(TAG, "Logout icon not found in layout");
        }

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        fetchPatients();
        fetchDoctors();

        FloatingActionButton fabAddPatient = findViewById(R.id.fabAddPatient);
        if (fabAddPatient != null) {
            fabAddPatient.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(PatientListActivity.this, RegisterActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_REGISTER);
                    Log.d(TAG, "Starting RegisterActivity for result");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start RegisterActivity: ", e);
                    Toast.makeText(this, "Error opening registration page: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.e(TAG, "FloatingActionButton not found in layout");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Fixed: removed 'Intent data'
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            fetchPatients();
            Log.d(TAG, "Registration successful, refreshing patient list");
        }
    }

    private void logout() {
        Log.d(TAG, "Initiating logout");
        Log.d(TAG, "SharedPreferences before logout: " + prefs.getAll().toString());
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor loginEditor = loginPrefs.edit();
                    loginEditor.clear();
                    loginEditor.commit();
                    Log.d(TAG, "LoginPrefs cleared: " + loginPrefs.getAll().toString());

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.putBoolean("is_logged_out", true);
                    editor.commit();
                    Log.d(TAG, "hcrs_prefs after logout: " + prefs.getAll().toString());

                    Intent intent = new Intent(PatientListActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("is_logged_out", true);
                    Log.d(TAG, "Starting LoginActivity with cleared task");
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "PatientListActivity finished");
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void fetchPatients() {
        apiService.getAllPatients().enqueue(new Callback<ApiResponse<List<Patient>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Patient>>> call, Response<ApiResponse<List<Patient>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Patient>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        patientList.clear();
                        patientList.addAll(apiResponse.getData());
                        patientAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Patients fetched: " + patientList.size());
                    } else {
                        Toast.makeText(PatientListActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Fetch patients failed: " + apiResponse.getMessage());
                    }
                } else {
                    Toast.makeText(PatientListActivity.this, "Failed to fetch patients", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Fetch patients response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Patient>>> call, Throwable t) {
                Toast.makeText(PatientListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Fetch patients network error: ", t);
            }
        });
    }

    private void fetchDoctors() {
        apiService.getDoctors().enqueue(new Callback<ApiResponse<List<DoctorSelection>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DoctorSelection>>> call, Response<ApiResponse<List<DoctorSelection>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    doctorList = response.body().getData();
                    Log.d(TAG, "Doctors fetched: " + (doctorList != null ? doctorList.size() : 0));
                } else {
                    Toast.makeText(PatientListActivity.this, "Failed to fetch doctors", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Fetch doctors failed: " + (response.body() != null ? response.body().getMessage() : "Response code " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DoctorSelection>>> call, Throwable t) {
                Toast.makeText(PatientListActivity.this, "Error fetching doctors: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Fetch doctors network error: ", t);
            }
        });
    }

    @Override
    public void onDeleteClick(Patient patient) {
        if (patient == null || patient.getName() == null || patient.getName().isEmpty() || patient.getPatientId() <= 0) {
            Log.e(TAG, "Invalid patient object on delete click: " + (patient == null ? "null" : "id=" + patient.getPatientId() + ", name=" + patient.getName()));
            Toast.makeText(this, "Error: Invalid patient data", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Delete Patient")
                .setMessage("Are you sure you want to delete " + patient.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> deletePatient(patient))
                .setNegativeButton("No", null)
                .show();
    }

    private void deletePatient(Patient patient) {
        if (patient == null || patient.getPatientId() <= 0) {
            Log.e(TAG, "Patient is null or invalid in deletePatient: " + (patient == null ? "null" : "id=" + patient.getPatientId()));
            Toast.makeText(this, "Error: Patient data missing", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Deleting patient with ID: " + patient.getPatientId());
        apiService.deletePatient(patient.getPatientId()).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    patientList.remove(patient);
                    patientAdapter.notifyDataSetChanged();
                    Toast.makeText(PatientListActivity.this, "Patient deleted successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Patient deleted: " + patient.getName());
                } else {
                    Toast.makeText(PatientListActivity.this, "Failed to delete patient: " + (response.body() != null ? response.body().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Delete patient failed: " + (response.body() != null ? response.body().getMessage() : "Response code " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(PatientListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Delete patient network error: ", t);
            }
        });
    }

    @Override
    public void onSetAppointmentClick(Patient patient) {
        if (doctorList == null || doctorList.isEmpty()) {
            Toast.makeText(this, "No doctors available", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "No doctors available for appointment");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_appointment, null);
        builder.setView(dialogView);

        Spinner spinnerDoctor = dialogView.findViewById(R.id.spinnerDoctor);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        if (spinnerDoctor == null || etDate == null || etTime == null || btnCancel == null || btnConfirm == null) {
            Log.e(TAG, "Dialog view elements not found");
            Toast.makeText(this, "UI error: Dialog setup failed", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayAdapter<DoctorSelection> doctorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctorList);
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctor.setAdapter(doctorAdapter);

        Calendar calendar = Calendar.getInstance();
        etDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(PatientListActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                etDate.setText(date);
            }, year, month, day).show();
        });

        etTime.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            new TimePickerDialog(PatientListActivity.this, (view, selectedHour, selectedMinute) -> {
                String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                etTime.setText(time);
            }, hour, minute, true).show();
        });

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            if (etDate.getText().toString().isEmpty() || etTime.getText().toString().isEmpty()) {
                Toast.makeText(PatientListActivity.this, "Please select date and time", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Date or time not selected");
                return;
            }
            DoctorSelection selectedDoctor = (DoctorSelection) spinnerDoctor.getSelectedItem();
            if (selectedDoctor == null) {
                Toast.makeText(PatientListActivity.this, "Please select a doctor", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "No doctor selected");
                return;
            }
            String appointmentDateTime = etDate.getText().toString() + " " + etTime.getText().toString();
            AppointmentRequest request = new AppointmentRequest(selectedDoctor.getDoctorId(), appointmentDateTime, "1");
            Log.d(TAG, "Setting appointment: patient_id=" + patient.getPatientId() + ", doctor_id=" + selectedDoctor.getDoctorId() + ", date=" + appointmentDateTime);
            setAppointment(patient.getPatientId(), request);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setAppointment(int patientId, AppointmentRequest request) {
        apiService.setAppointment(patientId, request).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(PatientListActivity.this, "Appointment set successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Appointment set successfully");
                } else {
                    Toast.makeText(PatientListActivity.this, "Failed to set appointment: " + (response.body() != null ? response.body().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Set appointment failed: " + (response.body() != null ? response.body().getMessage() : "Response code " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(PatientListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Set appointment network error: ", t);
            }
        });
    }
}