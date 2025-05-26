package com.example.hcrs.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.hcrs.data.entities.Receptionist;

import java.util.List;

@Dao
public interface ReceptionistDao {
    @Query("SELECT * FROM receptionist")
    List<Receptionist> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Receptionist> receptionists);

    @Insert
    void insert(Receptionist receptionist);

    @Delete
    void delete(Receptionist receptionist);
}
