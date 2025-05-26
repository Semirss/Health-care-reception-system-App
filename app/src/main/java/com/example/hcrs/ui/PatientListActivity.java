package com.example.hcrs.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.R;
import com.example.hcrs.adapter.PatientAdapter;
import com.example.hcrs.viewmodel.PatientViewModel;

public class PatientListActivity extends AppCompatActivity {

    private PatientViewModel viewModel;
    private PatientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_list);

        RecyclerView recyclerView = findViewById(R.id.patientRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PatientAdapter(this, new PatientAdapter.OnItemActionListener() {
            @Override
            public void onDelete(String id) {
                viewModel.deletePatient(id);
            }

            @Override
            public void onAddAppointment(String id) {
                // Implement your logic to add an appointment
            }
        });

        recyclerView.setAdapter(adapter);

        EditText search = findViewById(R.id.searchInput);
        search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        viewModel = new ViewModelProvider(this).get(PatientViewModel.class);
        viewModel.getPatients(this).observe(this, patients -> {
            adapter.setPatients(patients);
        });
    }
}
