package com.example.hcrs.data.entities;

import com.google.gson.annotations.SerializedName;

public class Person {
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("password")
    private String password;
    @SerializedName("role")
    private String role;

    public Person() {
        this.role = "";
        this.phoneNumber = "unknown";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null && !phoneNumber.isEmpty() ? phoneNumber : "unknown";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}