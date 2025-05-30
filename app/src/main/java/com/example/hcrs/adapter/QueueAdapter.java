package com.example.hcrs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.R;
import com.example.hcrs.data.entities.Que;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder> {
    private Context context;
    private List<Que> queueList = new ArrayList<>();

    public QueueAdapter(Context context) {
        this.context = context;
    }

    public void setQueueList(List<Que> queueList) {
        this.queueList = queueList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor_appointment, parent, false);
        return new QueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
        Que queue = queueList.get(position);
        String cardIdText = queue.getCardId() != 0 ? String.valueOf(queue.getCardId()) : "Unknown ID";
        String dateText = queue.getDate() != null ? queue.getDate() : "No Date";
        String statusText = queue.getStatus() != null ? (queue.isStatus() ? "Active" : "Inactive") : "Unknown Status";

        // Format date
        if (queue.getDate() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a");
                outputFormat.setTimeZone(TimeZone.getDefault());
                Date date = inputFormat.parse(queue.getDate());
                dateText = outputFormat.format(date);
            } catch (ParseException e) {
                android.util.Log.e("QueueAdapter", "Error parsing date: " + e.getMessage());
                dateText = queue.getDate(); // Fallback to raw date
            }
        }

        if (queue.getCardId() == 0) {
            android.util.Log.w("QueueAdapter", "Card ID is 0 at position: " + position);
        }
        if (queue.getDate() == null) {
            android.util.Log.w("QueueAdapter", "Date is null at position: " + position);
        }

        holder.cardIdTextView.setText("Card ID: " + cardIdText);
        holder.dateTextView.setText("Date: " + dateText);
        holder.statusTextView.setText("Status: " + statusText);
    }

    @Override
    public int getItemCount() {
        return queueList.size();
    }

    static class QueueViewHolder extends RecyclerView.ViewHolder {
        TextView cardIdTextView, dateTextView, statusTextView;

        QueueViewHolder(@NonNull View itemView) {
            super(itemView);
            cardIdTextView = itemView.findViewById(R.id.cardIdTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}