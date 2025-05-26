package com.example.hcrs.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "que")
public class Que {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int patientid;
    public String month;
}
