package com.example.hcrs.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "receptionist")
public class Receptionist {
    @PrimaryKey
    public int id;

    public String name;
    public String password;
}
