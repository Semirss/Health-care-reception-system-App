// File path: src/main/java/com/example/hcrs/api/ApiService.java

package com.example.hcrs.api;

import retrofit2.Call;
import retrofit2.http.*;

import com.example.hcrs.data.entities.Doctor;
import java.util.List;

import com.example.hcrs.data.entities.Receptionist;
import com.example.hcrs.wrapper.DoctorResponse;
import com.example.hcrs.wrapper.ResponseWrapper;

public interface ApiService {
    @GET("getDoctors")
    Call<DoctorResponse> getAllDoctors();
    @POST("addDoctor")
    Call<DoctorResponse> addDoctor(@Body Doctor doctor);

    @PUT("updateDoctor/{doctor_id}") // Updated to match server route
    Call<DoctorResponse> updateDoctor(@Path("doctor_id") int doctor_id, @Body Doctor doctor);
    @DELETE("deleteDoctor/{doctor_id}")
    Call<DoctorResponse> deleteDoctor(@Path("doctor_id") int doctor_id);
    // Receptionist endpoints
    @GET("getReceptionists")
    Call<ResponseWrapper<Receptionist>> getAllReceptionists();

    @POST("addReceptionist")
    Call<ResponseWrapper<Receptionist>> addReceptionist(@Body Receptionist receptionist);

    @PUT("updateReceptionist/{receptionist_id}")
    Call<ResponseWrapper<Receptionist>> updateReceptionist(@Path("receptionist_id") int receptionist_id, @Body Receptionist receptionist);
    @DELETE("deleteReceptionist/{receptionist_id}")
    Call<ResponseWrapper<Receptionist>> deleteReceptionist(@Path("receptionist_id") int receptionist_id);
}
