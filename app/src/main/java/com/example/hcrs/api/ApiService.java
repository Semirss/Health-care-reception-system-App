package com.example.hcrs.api;

import retrofit2.Call;
import retrofit2.http.*;

import com.example.hcrs.wrapper.DoctorIdResponse;
import com.example.hcrs.auth.LoginRequest;
import com.example.hcrs.data.entities.Doctor;
import java.util.List;
import java.util.Queue;

import com.example.hcrs.data.entities.Patient;
import com.example.hcrs.data.entities.Que;
import com.example.hcrs.data.entities.Receptionist;
import com.example.hcrs.data.entities.User;
import com.example.hcrs.wrapper.DoctorResponse;
import com.example.hcrs.wrapper.ResponseWrapper;
import com.example.hcrs.wrapper.loginresponse;

import org.json.JSONObject;

public interface ApiService {
    @GET("getDoctors")
    Call<DoctorResponse> getAllDoctors();
    @POST("addDoctor")
    Call<DoctorResponse> addDoctor(@Body Doctor doctor);

    @PUT("updateDoctor/{doctor_id}")
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

    // New login endpoint
    @POST("login")
    Call<loginresponse<User>> login(@Body LoginRequest loginRequest);

    @GET("queue/doctor/{doctor_id}")
    Call<loginresponse<Que[]>> getQueueByDoctorID(@Path("doctor_id") int doctorId);

    @GET("queue/patient/{patient_id}")
    Call<loginresponse<List<Que>>> getQueueByPatientID(@Path("patient_id") int patientId);

    @GET("patient/{card_id}")
    Call<loginresponse<Patient>> getPatientByCardID(@Path("card_id") int cardId);

    @GET("user/doctor_id")
    Call<loginresponse<Integer>> getDoctorId(@Query("name") String name, @Query("role") String role);

    @GET("getCardByID/{card_id}")
    Call<loginresponse<JSONObject>> getCardByID(@Path("card_id") int cardId);

    @POST("addFindings/{card_id}")
    Call<loginresponse<Void>> addFindingsToHistory(@Path("card_id") int cardId, @Body JSONObject body);

    @DELETE("queue/{queue_id}")
    Call<loginresponse<Void>> deleteQueue(@Path("queue_id") int queueId);
}
