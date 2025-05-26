package com.example.hcrs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.R;
import com.example.hcrs.data.entities.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

    private List<Patient> fullList;
    private List<Patient> filteredList;
    private final Context context;
    private List<Patient> patientList;

    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onDelete(String id);
        void onAddAppointment(String id);
    }

    // ✅ Updated constructor
    public PatientAdapter(Context ctx, List<Patient> initialList, OnItemActionListener l) {
        this.context = ctx;
        this.listener = l;
        this.fullList = new ArrayList<>(initialList);
        this.filteredList = new ArrayList<>(initialList);
    }

    public void updateList(List<Patient> newList) {
        this.fullList = new ArrayList<>(newList);
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
    public void setPatients(List<Patient> patients) {
        this.patientList.clear();
        this.patientList.addAll(patients);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredList = fullList.stream()
                .filter(p -> p.name.toLowerCase().contains(query.toLowerCase()) ||
                        p.contact.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_patient, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient p = filteredList.get(position);
        holder.name.setText(p.name);
        holder.phone.setText(p.contact);

        holder.delete.setOnClickListener(v -> listener.onDelete(String.valueOf(p.id)));
        holder.appointment.setOnClickListener(v -> listener.onAddAppointment(String.valueOf(p.id)));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;
        Button delete, appointment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.patientName);

        }
    }
}
