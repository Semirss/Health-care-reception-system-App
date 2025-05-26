package com.example.hcrs.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hcrs.data.entities.Doctor;

import java.util.List;

@Dao
public interface DoctorDao {
    @Query("SELECT * FROM doctor")
    List<Doctor> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Doctor> doctors);

    @Insert
    void insert(Doctor doctor);

    @Delete
    void delete(Doctor doctor);
}
