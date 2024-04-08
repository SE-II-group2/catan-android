package com.group2.catan_android.networking.repository;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catan_android.networking.api.GameApiClient;
import com.group2.catan_android.networking.api.GameApiService;
import com.group2.catan_android.networking.dto.ApiErrorResponse;
import com.group2.catan_android.networking.dto.Game;
import com.group2.catan_android.networking.dto.JoinGameRequest;
import com.group2.catan_android.networking.dto.JoinGameResponse;
import com.group2.catan_android.networking.dto.ListGameResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class GameRepository {
    //Todo: back with datasource
    private final GameApiService apiService;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final GameRepository instance = new GameRepository();

    private GameRepository(){
        this.apiService = GameApiClient.getGameApiService();
    }

    public static GameRepository getInstance(){
        return instance;
    }

    public void join(JoinGameRequest request, final JoinCallback callback, boolean create){
        Call<JoinGameResponse> call = create ? apiService.createAndJoin(request)
                : apiService.join(request);
        Log.d("message", call.request().url().toString());
        call.enqueue(new Callback<JoinGameResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<JoinGameResponse> call, Response<JoinGameResponse> response) {
                if(response.isSuccessful()){
                    JoinGameResponse successfulResponse = response.body();
                    callback.onJoin(successfulResponse);
                }else {
                    try (okhttp3.ResponseBody errorResponse = response.errorBody()) {
                        ApiErrorResponse apiError = mapper.readValue(errorResponse.string(), ApiErrorResponse.class);
                        callback.onJoinUnsuccessful(apiError);
                    }catch (Exception e){
                        Log.e("Network", "cannot Process response", e);
                    }

                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<JoinGameResponse> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void listGames(listCallback callback){
        Call<ListGameResponse> call = apiService.list();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ListGameResponse> call, Response<ListGameResponse> response) {
                if (response.isSuccessful()) {
                    ListGameResponse successfulResponse = response.body();
                    callback.onListReceived(successfulResponse);
                }
            }

            @Override
            public void onFailure(Call<ListGameResponse> call, Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }

    public interface JoinCallback {
        void onJoin(JoinGameResponse joinGameResponse);
        void onJoinUnsuccessful(ApiErrorResponse errorResponse);
        void onError(Throwable throwable);
    }

    public interface listCallback {
        void onListReceived(ListGameResponse response);
        void onFailure(Throwable throwable);
    }
}
