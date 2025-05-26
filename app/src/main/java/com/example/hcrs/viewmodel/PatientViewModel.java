//package com.example.hcrs.viewmodel;
//
//import android.app.Application;
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.example.hcrs.data.AppDatabase;
//import com.example.hcrs.data.dao.PatientDao;
//import com.example.hcrs.data.entities.Patient;
//import com.example.hcrs.network.ApiService;
//import com.example.hcrs.network.RetrofitClient;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class PatientViewModel extends AndroidViewModel {
//
//    private final PatientDao PatientDao;
//    private final ApiService apiService;
//    private final MutableLiveData<List<Patient>> patientsLiveData = new MutableLiveData<>();
//
//    public PatientViewModel(@NonNull Application application) {
//        super(application);
//        PatientDao = AppDatabase.getInstance(application).patientDao();
//
//    }
//
//    public LiveData<List<Patient>> getPatients(Context context) {
//        String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
//
//        if (isOnline(context)) {
//            apiService.getAllPatients().enqueue(new Callback<List<Patient>>() {
//                @Override
//                public void onResponse(@NonNull Call<List<Patient>> call, @NonNull Response<List<Patient>> response) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        List<Patient> list = response.body();
//                        patientsLiveData.setValue(list);
//
//                        // Save to local DB
//                        new Thread(() -> PatientDao.insertAll(list)).start();
//                    } else {
//                        loadOffline(currentMonth);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<Patient>> call, Throwable t) {
//                    loadOffline(currentMonth);
//                }
//            });
//        } else {
//            loadOffline(currentMonth);
//        }
//
//        return patientsLiveData;
//    }
//
//    private void loadOffline(String month) {
//        new Thread(() -> {
//            List<Patient> local = PatientDao.getPatientsForMonth(month);
//            patientsLiveData.postValue(local);
//        }).start();
//    }
//
//    private boolean isOnline(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnected();
//    }
//
//    public void deletePatient(String id) {
//        new Thread(() -> PatientDao.deleteById(id)).start();
//    }
//
//
//}
