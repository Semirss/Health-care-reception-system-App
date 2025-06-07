package com.example.hcrs.data.entities;

import com.google.gson.annotations.SerializedName;

public class DoctorSelection {
    @SerializedName("doctor_id")
    private int doctorId;
    private String name;

    public DoctorSelection(int doctorId, String name) {
        this.doctorId = doctorId;
        this.name = name;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}