package com.group2.catan_android.data.service;

import org.jetbrains.annotations.NotNull;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ApiService {
    private static final String BASE_URL = "http://10.0.2.2:8080/catan/game/";
    private final GameApi gameApi;
    private static ApiService instance;

    private ApiService(){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        gameApi = retrofit.create(GameApi.class);
    }
    @NotNull
    synchronized public static ApiService getInstance(){
        if(instance == null){
            instance = new ApiService();
        }
        return instance;
    }

    public GameApi getGameApi() {
        return gameApi;
    }
}
