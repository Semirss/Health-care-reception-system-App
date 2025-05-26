package com.example.hcrs.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "speciality")
public class Speciality {
    @PrimaryKey
    public int id;

    public String speciality;
}
