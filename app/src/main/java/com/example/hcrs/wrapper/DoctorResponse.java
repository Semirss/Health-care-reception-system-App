package com.example.hcrs.wrapper;

import com.example.hcrs.data.entities.Doctor;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DoctorResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private List<Doctor> data;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Doctor> getData() {
        return data;
    }

    public void setData(List<Doctor> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}