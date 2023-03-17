package com.example.ecologyproject.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecologyproject.model.Station;
import com.example.ecologyproject.remote.RetrofitClient;
import com.example.ecologyproject.service.StationService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StationViewModel extends ViewModel {
    private MutableLiveData<List<Station>> stations;

    public LiveData<List<Station>> getStations() {
        if (stations == null) {
            stations = new MutableLiveData<>();
            loadStations();
        }
        return stations;
    }

    public void loadStations() {
        StationService stationService = RetrofitClient.getRetrofit().create(StationService.class);
        Call<List<Station>> call = stationService.getStations();
        call.enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                stations.setValue(response.body());

                Log.d("loadStations", String.valueOf(response.body()));
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                Log.d("StationViewModel", "Error: " + t.getMessage());
            }
        });
    }
}

