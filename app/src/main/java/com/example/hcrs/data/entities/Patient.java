package com.example.hcrs.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "patient")
public class Patient {
    @PrimaryKey
    @ColumnInfo(name = "patient_id")
    private int patient_id;

    @ColumnInfo(name = "name")
    private String name;

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}