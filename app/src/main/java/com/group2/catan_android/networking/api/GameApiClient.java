package com.group2.catan_android.networking.api;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GameApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/catan/game/";
    private static GameApiService gameApiService;
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private GameApiClient(){}

    public static GameApiService getGameApiService(){
        if(gameApiService == null){
            gameApiService = retrofit.create(GameApiService.class);
        }
        return gameApiService;
    }
}
