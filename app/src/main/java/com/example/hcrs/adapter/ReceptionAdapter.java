package com.example.hcrs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.R;
import com.example.hcrs.data.entities.Receptionist;

import java.util.List;

public class ReceptionAdapter extends RecyclerView.Adapter<ReceptionAdapter.ReceptionistViewHolder> {

    private List<Receptionist> receptionists;
    private final OnReceptionistClickListener listener;

    public interface OnReceptionistClickListener {
        void onEditClick(Receptionist receptionist);
        void onDeleteClick(Receptionist receptionist);
    }

    public ReceptionAdapter(List<Receptionist> receptionists, OnReceptionistClickListener listener) {
        this.receptionists = receptionists;
        this.listener = listener;
    }

    public void updateList(List<Receptionist> newReceptionists) {
        this.receptionists = newReceptionists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReceptionistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receptionist_admin, parent, false);
        return new ReceptionistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceptionistViewHolder holder, int position) {
        Receptionist receptionist = receptionists.get(position);
        holder.bind(receptionist, listener);
    }

    @Override
    public int getItemCount() {
        return receptionists.size();
    }

    static class ReceptionistViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvAddress, tvPhone;
        Button btnEdit, btnDelete;

        ReceptionistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Receptionist receptionist, OnReceptionistClickListener listener) {
            tvName.setText(receptionist.getName());
            tvEmail.setText(receptionist.getEmail());
            tvAddress.setText(receptionist.getAddress());
            tvPhone.setText(receptionist.getPhone());
            btnEdit.setOnClickListener(v -> listener.onEditClick(receptionist));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(receptionist));
        }
    }
}