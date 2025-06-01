package com.example.hcrs.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcrs.R;
import com.example.hcrs.api.ApiService;
import com.example.hcrs.data.entities.Que;
import com.example.hcrs.network.RetrofitClient;
import com.example.hcrs.wrapper.loginresponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientQueueAdapter extends RecyclerView.Adapter<PatientQueueAdapter.QueueViewHolder> {
    private Context context;
    private List<Que> queueList = new ArrayList<>();

    public PatientQueueAdapter(Context context) {
        this.context = context;
    }

    public void setQueueList(List<Que> queueList) {
        this.queueList = new ArrayList<>(queueList);
        notifyDataSetChanged();
        for (Que queue : queueList) {
            if (queue.getDate() != null && !queue.getDate().isEmpty() && queue.isStatus()) {
                scheduleNotification(queue);
            }
        }
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_patient_page, parent, false);
        return new QueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
        Que queue = queueList.get(position);
        holder.bind(queue);
    }

    @Override
    public int getItemCount() {
        return queueList.size();
    }

    private void scheduleNotification(Que queue) {
        try {
            String dateString = queue.getDate();
            if (dateString == null || dateString.isEmpty()) {
                android.util.Log.w("PatientQueueAdapter", "Empty date for queueId: " + queue.getQueueId());
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("EAT"));
            Date queueDate = sdf.parse(dateString);
            if (queueDate == null) {
                android.util.Log.w("PatientQueueAdapter", "Failed to parse date for queueId: " + queue.getQueueId());
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(queueDate);
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                android.util.Log.d("PatientQueueAdapter", "Skipping past date for queueId: " + queue.getQueueId());
                return;
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    android.util.Log.w("PatientQueueAdapter", "Cannot schedule exact alarms for queueId: " + queue.getQueueId());
                    Toast.makeText(context, "Please enable exact alarm permission in settings", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            Intent intent = new Intent(context, com.example.hcrs.NotificationReceiver.class);
            intent.putExtra("queueId", queue.getQueueId());
            intent.putExtra("message", "Appointment Reminder: Your appointment with Dr. " + queue.getDoctorName() + " is today at " + dateString);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    queue.getQueueId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            try {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
                android.util.Log.d("PatientQueueAdapter", "Scheduled notification for queueId: " + queue.getQueueId() + " at " + sdf.format(queueDate));
            } catch (SecurityException e) {
                android.util.Log.e("PatientQueueAdapter", "SecurityException scheduling alarm: " + e.getMessage());
                Toast.makeText(context, "Failed to schedule notification due to permission issue", Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            android.util.Log.e("PatientQueueAdapter", "Error parsing date for notification: " + e.getMessage());
        }
    }

    class QueueViewHolder extends RecyclerView.ViewHolder {
        TextView doctorIdTextView, doctorNameTextView, dateTextView, statusTextView;
        Button btnSeeDetails, btnCancel;

        QueueViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorIdTextView = itemView.findViewById(R.id.doctorIdTextView);
            doctorNameTextView = itemView.findViewById(R.id.doctorNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            btnSeeDetails = itemView.findViewById(R.id.seeDetailsButton);
            btnCancel = itemView.findViewById(R.id.cancelButton);
        }

        void bind(Que queue) {
            String doctorIdText = queue.getDoctorId() != 0 ? String.valueOf(queue.getDoctorId()) : "Unknown ID";
            String doctorNameText = queue.getDoctorName() != null ? queue.getDoctorName() : "Unknown Doctor";
            String dateText = queue.getDate() != null ? queue.getDate() : "No Date";
            String statusText = queue.getStatus() != null ? (queue.isStatus() ? "Active" : "Inactive") : "Unknown Status";

            // Format date
            if (queue.getDate() != null) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
                    outputFormat.setTimeZone(TimeZone.getTimeZone("EAT"));
                    Date date = inputFormat.parse(queue.getDate());
                    dateText = outputFormat.format(date);
                } catch (ParseException e) {
                    android.util.Log.e("PatientQueueAdapter", "Error parsing queue date: " + e.getMessage());
                    dateText = queue.getDate();
                }
            }

            if (queue.getDoctorId() == 0) {
                android.util.Log.w("PatientQueueAdapter", "Doctor ID is 0 at position: " + getAdapterPosition());
            }
            if (queue.getDoctorName() == null) {
                android.util.Log.w("PatientQueueAdapter", "Doctor name is null at position: " + getAdapterPosition());
            }
            if (queue.getDate() == null) {
                android.util.Log.w("PatientQueueAdapter", "Date is null at position: " + getAdapterPosition());
            }

            doctorIdTextView.setText("Doctor ID: " + doctorIdText);
            doctorNameTextView.setText("Doctor: " + doctorNameText);
            dateTextView.setText("Date: " + dateText);
            statusTextView.setText("Status: " + statusText);

            btnSeeDetails.setOnClickListener(v -> showCardDetailsDialog(queue.getCardId()));
            btnCancel.setOnClickListener(v -> deleteQueue(queue.getQueueId(), getAdapterPosition()));
        }

        private void showCardDetailsDialog(int cardId) {
            try {
                View dialogView = LayoutInflater.from(context).inflate(R.layout.patient_dialog_card_details, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(dialogView);

                TextView tvPatientId = dialogView.findViewById(R.id.tv_patient_id);
                TextView tvName = dialogView.findViewById(R.id.tv_name);
                TextView tvDate = dialogView.findViewById(R.id.tv_date);
                TextView tvHistory = dialogView.findViewById(R.id.tv_history);
                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);



                AlertDialog dialog = builder.create();

                ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                Call<loginresponse<JSONObject>> call = apiService.getCardByID(cardId);

                call.enqueue(new Callback<loginresponse<JSONObject>>() {
                    @Override
                    public void onResponse(Call<loginresponse<JSONObject>> call, Response<loginresponse<JSONObject>> response) {
                        try {
                            String rawResponse = response.body() != null ? response.body().toString() : "Response body is null";
                            android.util.Log.d("PatientQueueAdapter", "Raw response: " + rawResponse);

                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                JSONObject cardData = response.body().getData();
                                android.util.Log.d("PatientQueueAdapter", "Parsed cardData: " + (cardData != null ? cardData.toString() : "null"));

                                if (cardData != null && cardData.length() > 0) {
                                    if (cardData.has("nameValuePairs")) {
                                        cardData = cardData.optJSONObject("nameValuePairs");
                                        android.util.Log.d("PatientQueueAdapter", "Extracted nameValuePairs: " + (cardData != null ? cardData.toString() : "null"));
                                    }

                                    String patientId = cardData.has("patient_id") ? cardData.optString("patient_id", "N/A") : "N/A";
                                    String name = cardData.has("name") ? cardData.optString("name", "N/A") : "N/A";
                                    String dateStr = cardData.has("date") ? cardData.optString("date", "N/A") : "N/A";

                                    android.util.Log.d("PatientQueueAdapter", "Fields - patient_id: " + patientId + ", name: " + name + ", date: " + dateStr);

                                    tvPatientId.setText("Patient ID: " + patientId);
                                    tvName.setText("Name: " + name);

                                    if (!dateStr.equals("N/A") && !dateStr.isEmpty()) {
                                        try {
                                            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                            sdfInput.setTimeZone(TimeZone.getTimeZone("UTC"));
                                            SimpleDateFormat sdfOutput = new SimpleDateFormat("MMM dd, yyyy, hh:mm a");
                                            sdfOutput.setTimeZone(TimeZone.getTimeZone("EAT"));
                                            Date date = sdfInput.parse(dateStr);
                                            tvDate.setText("Date: " + sdfOutput.format(date));
                                        } catch (ParseException e) {
                                            tvDate.setText("Date: " + dateStr);
                                            android.util.Log.e("PatientQueueAdapter", "Error parsing date: " + e.getMessage());
                                        }
                                    } else {
                                        tvDate.setText("Date: N/A");
                                    }

                                    String historyStr = cardData.has("history") ? cardData.optString("history", "[]") : "[]";
                                    android.util.Log.d("PatientQueueAdapter", "History: " + historyStr);
                                    try {
                                        if (!historyStr.startsWith("[")) {
                                            historyStr = "[" + historyStr + "]";
                                        }
                                        JSONArray history = new JSONArray(historyStr);
                                        StringBuilder historyText = new StringBuilder();
                                        boolean hasValidEntry = false;

                                        for (int i = 0; i < history.length(); i++) {
                                            try {
                                                JSONObject historyObj = history.getJSONObject(i);
                                                String findings = historyObj.optString("new_findings", "");
                                                String prescribed = historyObj.optString("prescribed", "");
                                                android.util.Log.d("PatientQueueAdapter", "Entry " + i + ": findings=" + findings + ", prescribed=" + prescribed);

                                                if (!findings.isEmpty() || !prescribed.isEmpty()) {
                                                    hasValidEntry = true;
                                                    historyText.append("Findings: ")
                                                            .append(findings.isEmpty() ? "N/A" : findings)
                                                            .append("\nPrescribed: ")
                                                            .append(prescribed.isEmpty() ? "N/A" : prescribed)
                                                            .append("\n\n");
                                                }
                                            } catch (Exception e) {
                                                android.util.Log.w("PatientQueueAdapter", "Skipping invalid history entry " + i + ": " + e.getMessage());
                                            }
                                        }
                                        tvHistory.setText(hasValidEntry ? historyText.toString() : "No valid history available");
                                    } catch (Exception e) {
                                        tvHistory.setText("Error parsing history");
                                        android.util.Log.e("PatientQueueAdapter", "Error parsing history: " + e.getMessage());
                                    }
                                } else {
                                    tvPatientId.setText("Patient ID: N/A");
                                    tvName.setText("Name: N/A");
                                    tvDate.setText("Date: N/A");
                                    tvHistory.setText("No card data available");
                                    android.util.Log.e("PatientQueueAdapter", "cardData is null or empty");
                                }
                            } else {
                                String errorMsg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                                tvPatientId.setText("Patient ID: N/A");
                                tvName.setText("Name: N/A");
                                tvDate.setText("Date: N/A");
                                tvHistory.setText("Failed to load card details: " + errorMsg);
                                android.util.Log.e("PatientQueueAdapter", "Card fetch failed: " + errorMsg);
                            }
                        } catch (Exception e) {
                            tvPatientId.setText("Patient ID: N/A");
                            tvName.setText("Name: N/A");
                            tvDate.setText("Date: N/A");
                            tvHistory.setText("Error loading card data: " + e.getMessage());
                            android.util.Log.e("PatientQueueAdapter", "Response handling error: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<loginresponse<JSONObject>> call, Throwable t) {
                        tvPatientId.setText("Patient ID: N/A");
                        tvName.setText("Name: N/A");
                        tvDate.setText("Date: N/A");
                        tvHistory.setText("Network error: " + t.getMessage());
                        android.util.Log.e("PatientQueueAdapter", "Network error: " + t.getMessage());
                    }
                });

                btnCancel.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            } catch (Exception e) {
                android.util.Log.e("PatientQueueAdapter", "Error showing dialog: " + e.getMessage());
                Toast.makeText(context, "Error loading card details", Toast.LENGTH_SHORT).show();
            }
        }

        private void deleteQueue(int queueId, int position) {
            try {
                android.util.Log.d("PatientQueueAdapter", "Attempting to delete queueId: " + queueId + " at position: " + position);
                ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                Call<loginresponse<Void>> call = apiService.deleteQueue(queueId);

                call.enqueue(new Callback<loginresponse<Void>>() {
                    @Override
                    public void onResponse(Call<loginresponse<Void>> call, Response<loginresponse<Void>> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                android.util.Log.d("PatientQueueAdapter", "Queue item deleted: queueId=" + queueId);
                                if (position >= 0 && position < queueList.size()) {
                                    queueList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, queueList.size());
                                    android.util.Log.d("PatientQueueAdapter", "UI updated, new list size: " + queueList.size());
                                    cancelNotification(queueId);
                                } else {
                                    android.util.Log.w("PatientQueueAdapter", "Invalid position: " + position + ", full refresh");
                                    notifyDataSetChanged();
                                }
                                Toast.makeText(context, "Queue item deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = response.body() != null ? response.body().getMessage() : "Error: HTTP " + response.code();
                                android.util.Log.e("PatientQueueAdapter", "Failed to delete queue: " + message);
                                Toast.makeText(context, "Failed to delete queue: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            android.util.Log.e("PatientQueueAdapter", "Error processing delete response: " + e.getMessage());
                            Toast.makeText(context, "Error deleting queue: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<loginresponse<Void>> call, Throwable t) {
                        android.util.Log.e("PatientQueueAdapter", "Network error deleting queue: " + t.getMessage());
                        Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                android.util.Log.e("PatientQueueAdapter", "Error initiating delete: " + e.getMessage());
                Toast.makeText(context, "Error deleting queue: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        private void cancelNotification(int queueId) {
            Intent intent = new Intent(context, com.example.hcrs.NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    queueId,
                    intent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
            );
            if (pendingIntent != null) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
                android.util.Log.d("PatientQueueAdapter", "Cancelled notification for queueId: " + queueId);
            }
        }
    }
}