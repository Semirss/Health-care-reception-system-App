package com.example.hcrs.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "patient")
public class Patient {
    @PrimaryKey
    @NonNull
    public String id;

    public String name;
    public String contact;
    public String password;

    @ColumnInfo(name = "createdAt")
    public long createdAt; // Store timestamp as long (System.currentTimeMillis())

    public Patient(@NonNull String id, String name, String contact, String password) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.password = password;
        this.createdAt = System.currentTimeMillis(); // default time set
    }
}
