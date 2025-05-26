package com.example.hcrs.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hcrs.data.entities.MedicalRecord;

import java.util.List;


@Dao
public interface MedicalRecordDao {
    @Query("SELECT * FROM medicalrecord")
    List<MedicalRecord> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MedicalRecord> records);

    @Insert
    void insert(MedicalRecord record);

    @Delete
    void delete(MedicalRecord record);
}
