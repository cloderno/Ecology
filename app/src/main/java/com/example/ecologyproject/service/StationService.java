package com.example.ecologyproject.service;

import com.example.ecologyproject.model.Station;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StationService {
    @GET("api/air_indices/stations")
    Call<List<Station>> getStations();
}
