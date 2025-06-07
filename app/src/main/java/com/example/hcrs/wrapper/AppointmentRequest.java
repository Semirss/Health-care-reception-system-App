package com.example.hcrs.wrapper;

public class AppointmentRequest {
    private int doctor_id;
    private String date;
    private String status;

    public AppointmentRequest(int doctor_id, String date, String status) {
        this.doctor_id = doctor_id;
        this.date = date;
        this.status = status;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}