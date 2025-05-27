package com.example.hcrs.wrapper;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class loginresponse<T>{

    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private T data;
    @SerializedName("message")
    private String message;

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
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    }

