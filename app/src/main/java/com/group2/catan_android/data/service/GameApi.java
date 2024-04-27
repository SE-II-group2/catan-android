package com.group2.catan_android.data.service;

import com.group2.catan_android.data.api.JoinGameRequest;
import com.group2.catan_android.data.api.JoinGameResponse;
import com.group2.catan_android.data.api.ListGamesResponse;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GameApi {
    @GET("list")
    Single<ListGamesResponse> getLobbies();

    @POST("create")
    Single<JoinGameResponse> createGame();

    @POST("connect")
    Single<JoinGameResponse> connectGame(@Body JoinGameRequest request);

    @POST("create")
    Single<JoinGameResponse> createGame(@Body JoinGameRequest request);
}
