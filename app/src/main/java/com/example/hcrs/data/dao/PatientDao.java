package com.example.hcrs.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hcrs.data.entities.Patient;

import java.util.List;

@Dao
public interface PatientDao {
    @Query("SELECT * FROM patient")
    LiveData<List<Patient>> getAllPatients();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Patient patient);

    @Delete
    void delete(Patient patient);
    @Query("SELECT * FROM patient WHERE strftime('%m-%Y', createdAt) = :month")
    List<Patient> getPatientsForMonth(String month);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Patient> patients);

    @Query("DELETE FROM patient WHERE id = :id")
    void deleteById(String id);

}

