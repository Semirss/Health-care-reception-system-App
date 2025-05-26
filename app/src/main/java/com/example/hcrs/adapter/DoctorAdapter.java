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
import com.example.hcrs.data.entities.Doctor;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    private Context context;
    private List<Doctor> doctorList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Doctor doctor);
        void onDeleteClick(Doctor doctor);
    }

    public DoctorAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
        if (context instanceof OnItemClickListener) {
            this.listener = (OnItemClickListener) context;
        }
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.tvName.setText(doctor.getName());
        holder.tvEmail.setText(doctor.getEmail());
        holder.tvSpecialization.setText(doctor.getSpecialization());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(doctor);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvSpecialization;
        Button btnDelete;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDoctorName);
            tvEmail = itemView.findViewById(R.id.tvDoctorEmail);
            tvSpecialization = itemView.findViewById(R.id.tvDoctorSpecialization);
            btnDelete = itemView.findViewById(R.id.btnDeleteDoctor);
        }
    }
}