package com.example.hcrs.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

@Entity(tableName = "patient")
public class Patient extends Person {
    @PrimaryKey
    @ColumnInfo(name = "patient_id")
    @SerializedName("patient_id")
    private int patientId;

    @SerializedName("address")
    private String address;

    @SerializedName("history")
    private HashMap<String, Object> history;

    @SerializedName("date")
    private String date;

    public Patient() {
        super();
        setRole("patient");
        this.history = new HashMap<>();
        this.address = "";
        this.date = "";
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address : "";
    }

    public HashMap<String, Object> getHistory() {
        return history;
    }

    public void setHistory(HashMap<String, Object> history) {
        this.history = history != null ? history : new HashMap<>();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date != null ? date : "";
    }
}