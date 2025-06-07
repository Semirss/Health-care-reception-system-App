package com.example.hcrs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.R;
import com.example.hcrs.data.entities.Patient;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
    private List<Patient> patientList;
    private OnPatientActionListener listener;

    public interface OnPatientActionListener {
        void onDeleteClick(Patient patient);
        void onSetAppointmentClick(Patient patient);
    }

    public PatientAdapter(List<Patient> patientList, OnPatientActionListener listener) {
        this.patientList = patientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        holder.patientName.setText(patient.getName());
        holder.patientPhone.setText(patient.getPhoneNumber());
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(patient));
        holder.btnSetAppointment.setOnClickListener(v -> listener.onSetAppointmentClick(patient));
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, patientPhone;
        ImageButton btnDelete, btnSetAppointment;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patientName);
            patientPhone = itemView.findViewById(R.id.patientPhone);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnSetAppointment = itemView.findViewById(R.id.btnSetAppointment);
        }
    }
}