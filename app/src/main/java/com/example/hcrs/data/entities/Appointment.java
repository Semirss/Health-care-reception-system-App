package com.example.hcrs.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointment")
public class Appointment {
    @PrimaryKey
    public int id;

    public int patientid;
    public int doctorid;
    public String month;
    public String date;
    public String time;
}
