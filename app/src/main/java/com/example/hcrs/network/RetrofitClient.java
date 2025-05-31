package com.example.hcrs.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.8.124:7000/api/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Create a Gson instance with a custom TypeAdapter for JSONObject
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(JSONObject.class, new TypeAdapter<JSONObject>() {
                        @Override
                        public void write(JsonWriter out, JSONObject value) throws IOException {
                            // Serialize JSONObject to JSON string
                            out.jsonValue(value != null ? value.toString() : "null");
                        }

                        @Override
                        public JSONObject read(JsonReader in) throws IOException {
                            // Read the JSON object directly
                            try {
                                // Use Gson's JsonParser to read the JSON object
                                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                                com.google.gson.JsonElement jsonElement = parser.parse(in);
                                return new JSONObject(jsonElement.getAsJsonObject().toString());
                            } catch (Exception e) {
                                throw new IOException("Error parsing JSONObject: " + e.getMessage(), e);
                            }
                        }
                    })
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}