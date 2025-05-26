package com.example.hcrs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.adapter.PatientAdapter;
import com.example.hcrs.data.entities.Patient;
import com.example.hcrs.viewmodel.PatientViewModel;

import java.util.ArrayList;
import java.util.List;

public class PatientListActivity extends AppCompatActivity implements PatientAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private EditText searchInput;
    private PatientAdapter adapter;
    private PatientViewModel viewModel;
    private List<Patient> fullPatientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_list);

        recyclerView = findViewById(R.id.patientRecyclerView);
        searchInput = findViewById(R.id.searchInput);

        adapter = new PatientAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        // Observe Room database
        viewModel.getAllPatients().observe(this, new Observer<List<Patient>>() {
            @Override
            public void onChanged(List<Patient> patients) {
                fullPatientList = patients;
                adapter.updateList(patients);
            }
        });

        // Search filter
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPatients(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    @Override
    public void onDelete(String id) {
        // TODO: handle delete logic here
    }

    @Override
    public void onAddAppointment(String id) {
        // TODO: handle add appointment logic here
    }

    private void filterPatients(String keyword) {
        List<Patient> filtered = new ArrayList<>();
        for (Patient p : fullPatientList) {
            if (p.name.toLowerCase().contains(keyword.toLowerCase()) ||
                    p.contact.contains(keyword)) {
                filtered.add(p);
            }
        }
        adapter.updateList(filtered);
    }
}
