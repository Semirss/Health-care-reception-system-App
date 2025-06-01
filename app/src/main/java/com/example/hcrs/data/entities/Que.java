package com.example.hcrs.data.entities;

import com.google.gson.annotations.SerializedName;

public class Que {
    @SerializedName("queue_id")
    private int queueId;

    @SerializedName("card_id")
    private int cardId;

    @SerializedName("date")
    private String date;

    @SerializedName("status")
    private String status;

    @SerializedName("doctor_id")
    private int doctorId;

    @SerializedName("doctor_name")
    private String doctorName;

    // Getter for queueId
    public int getQueueId() {
        return queueId;
    }

    // Setter for queueId
    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    // Getter for cardId
    public int getCardId() {
        return cardId;
    }

    // Setter for cardId
    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    // Getter for date
    public String getDate() {
        return date;
    }

    // Setter for date
    public void setDate(String date) {
        this.date = date;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    // Getter to convert status to boolean
    public boolean isStatus() {
        return status != null && ("Active".equalsIgnoreCase(status) || "1".equals(status));
    }

    // Getter for doctorId
    public int getDoctorId() {
        return doctorId;
    }

    // Setter for doctorId
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    // Getter for doctorName
    public String getDoctorName() {
        return doctorName;
    }

    // Setter for doctorName
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}