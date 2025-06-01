package com.example.hcrs.data.entities;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("person_id")
    private int personId;
    private String name;
    private String role;
    @SerializedName("patient_id")
    private Integer patientId; // Nullable for non-patients

    public User(int personId, String name, String role, Integer patientId) {
        this.personId = personId;
        this.name = name;
        this.role = role;
        this.patientId = patientId;
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

    public Integer getPatientId() {
        return patientId;
    }
}