package com.example.ecologyproject.service;

import com.example.ecologyproject.model.Region;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RegionService {
    @GET("api/air_indices/regions")
    Call<List<Region>> getRegions();
}
