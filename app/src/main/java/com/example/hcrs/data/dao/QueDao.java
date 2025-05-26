package com.example.hcrs.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.hcrs.data.entities.Que;

import java.util.List;

@Dao
public interface QueDao {
    @Query("SELECT * FROM que")
    List<Que> getAll();

    @Query("SELECT * FROM que WHERE month = :currentMonth")
    List<Que> getByMonth(String currentMonth);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Que> queues);

    @Insert
    void insert(Que que);

    @Delete
    void delete(Que que);
}
