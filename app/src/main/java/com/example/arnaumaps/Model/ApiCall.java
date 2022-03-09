package com.example.arnaumaps.Model;

import com.example.arnaumaps.ModelFlickr.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiCall {
    String apikey = "AIzaSyAQEsvRsrbXXBYcnCB81N6LC7J_wG001t4";

    @GET("json/{lat}/{lng}")
    Call<ModelApi> getAddress(@Path("lat") String lat, @Path("lng") String lng);

    @GET("?method=flickr.photos.search&api_key=" + apikey + "&format=json&nojsoncallback=1")
    Call<Results> getData(@Query("lat") String lat, @Query("lon") String lon);
}