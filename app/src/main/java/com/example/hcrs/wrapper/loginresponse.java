package com.example.hcrs.wrapper;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class loginresponse<T> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private T data;

    @SerializedName("message")
    private String message;

    @SerializedName("doctor_id")
    private Integer doctorId; // Use Integer to handle null cases

    public loginresponse() {
        // Default constructor for Gson
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message != null ? message : "Unknown error";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getDoctorId() {
        return doctorId != null ? doctorId : 0; // Default to 0 if null
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    // Add this method to help debug the raw JSON response
    public String getRawDataAsString() {
        if (data == null) return "null";
        Gson gson = new Gson();
        return gson.toJson(data);
    }

    @Override
    public String toString() {
        return "loginresponse{" +
                "success=" + success +
                ", data=" + getRawDataAsString() +
                ", message='" + message + '\'' +
                ", doctorId=" + doctorId +
                '}';
    }
}