package com.example.hcrs.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.hcrs.data.dao.PatientDao;
import com.example.hcrs.data.entities.Patient;

@Database(entities = {Patient.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract PatientDao patientDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "healthcare_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
