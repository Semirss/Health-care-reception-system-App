package com.example.hcrs.network;

import com.example.hcrs.data.entities.Patient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/api/patients")
    Call<List<Patient>> getAllPatients();
}
