package com.example.hcrs.data.entities;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("person_id")
    private int personId;
    private String name;
    private String role;

    public User(int personId, String name, String role) {
        this.personId = personId;
        this.name = name;
        this.role = role;
    }

    public int getPersonId() {
        return personId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}