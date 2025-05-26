package com.example.hcrs.wrapper;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseWrapper<T> {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private List<T> data;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}