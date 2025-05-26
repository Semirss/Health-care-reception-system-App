package com.example.hcrs.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.hcrs.data.entities.Speciality;

import java.util.List;

@Dao
public interface SpecialityDao {
    @Query("SELECT * FROM speciality")
    List<Speciality> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Speciality> specialities);

    @Insert
    void insert(Speciality speciality);

    @Delete
    void delete(Speciality speciality);
}
