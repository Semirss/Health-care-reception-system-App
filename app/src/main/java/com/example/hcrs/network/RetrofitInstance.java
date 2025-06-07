package com.example.hcrs.network;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.8.124:7000/api/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(
                            new GsonBuilder()
                                    .serializeNulls()
                                    .create()
                    ))
                    .build();
        }
        return retrofit;
    }
}