package com.group2.catan_android.networking.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.group2.catan_android.networking.dto.JoinGameRequest;
import com.group2.catan_android.networking.dto.JoinGameResponse;
import com.group2.catan_android.networking.dto.ListGameResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


@JsonIgnoreProperties(ignoreUnknown=true)
public interface GameApiService {
    @POST("create")
    Call<JoinGameResponse> createAndJoin(@Body JoinGameRequest request);

    @POST("connect")
    Call<JoinGameResponse> join(@Body JoinGameRequest request);

    @GET("list")
    Call<ListGameResponse> list();

}
