package com.example.hcrs.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hcrs.data.entities.Appointment;

import java.util.List;
@Dao
public interface AppointmentDao {
    @Query("SELECT * FROM appointment")
    List<Appointment> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Appointment> appointments);

    @Insert
    void insert(Appointment appointment);

    @Delete
    void delete(Appointment appointment);
}
