package com.example.ecologyproject.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecologyproject.model.Region;
import com.example.ecologyproject.remote.RetrofitClient;
import com.example.ecologyproject.service.RegionService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegionViewModel extends ViewModel {
    private MutableLiveData<List<Region>> regions;

    public LiveData<List<Region>> getRegions() {
        if (regions == null) {
            regions = new MutableLiveData<>();
            loadRegions();
        }
        return regions;
    }

    void loadRegions() {
        RegionService regionService = RetrofitClient.getRetrofit().create(RegionService.class);
        Call<List<Region>> call = regionService.getRegions();
        call.enqueue(new Callback<List<Region>>() {
            @Override
            public void onResponse(Call<List<Region>> call, Response<List<Region>> response) {
                regions.setValue(response.body());
                Log.d("loadRegions", "Ww" + response.body().toString());
            }

            @Override
            public void onFailure(Call<List<Region>> call, Throwable t) {
                Log.d("RegionViewModel", "Error: " + t.getMessage());
            }
        });
    }
}
