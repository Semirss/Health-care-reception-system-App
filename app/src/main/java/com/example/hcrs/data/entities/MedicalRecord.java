package com.example.hcrs.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicalrecord")
public class MedicalRecord {
    @PrimaryKey
    public int id;

    public int ptientid;
    public String notes;
    public String timestamp;
    public String symptom;
}
