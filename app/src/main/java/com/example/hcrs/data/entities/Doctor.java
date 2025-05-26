package com.example.hcrs.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "doctor")
public class Doctor {
    @PrimaryKey
    public int id;

    public String name;
    public String password;
    public String speciality;
    public String roomnumber;
}
