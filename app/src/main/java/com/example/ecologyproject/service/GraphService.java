package com.example.ecologyproject.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GraphService {
    @GET("webview/air_graph")
    Call<ResponseBody> getGraph(@Query("air_station_id") int id, @Query("code") String code);
}
