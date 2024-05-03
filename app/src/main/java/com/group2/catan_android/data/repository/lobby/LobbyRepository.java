package com.group2.catan_android.data.repository.lobby;

import android.util.Log;

import com.group2.catan_android.data.api.ApiErrorResponse;
import com.group2.catan_android.data.api.JoinGameRequest;
import com.group2.catan_android.data.api.JoinGameResponse;
import com.group2.catan_android.data.api.ListGamesResponse;
import com.group2.catan_android.data.model.AvailableGame;
import com.group2.catan_android.data.service.GameApi;
import com.group2.catan_android.data.util.ObjectMapperProvider;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class LobbyRepository implements LobbyLoader, LobbyJoiner{
    private static LobbyRepository instance;
    private final GameApi api;
    private LobbyRepository(GameApi gameApi){
        this.api = gameApi;
    }
    @NotNull
    synchronized public static LobbyRepository getInstance(){
        return instance;
    }

    public static void initialize(@NotNull GameApi api){
        if(instance == null){
            instance = new LobbyRepository(api);
        }
    }

    public Single<List<AvailableGame>> getLobbies(){
        return api.getLobbies()
                .map(ListGamesResponse::getGameList)
                .onErrorResumeNext(throwable -> Single.error(convertAPIError(throwable)));
    }

    public Single<JoinGameResponse> joinGame(JoinGameRequest joinRequest){
        return api.connectGame(joinRequest)
                .onErrorResumeNext(throwable -> Single.error(convertAPIError(throwable)));
    }

    public Single<JoinGameResponse> createGame(JoinGameRequest joinGameRequest){
        Log.d("Test", "Create Game Called");
        return api.createGame(joinGameRequest)
                .onErrorResumeNext(throwable -> Single.error(convertAPIError(throwable)));
    }

    public Completable leaveGame(String token){
        return api.leaveGame(token)
                .onErrorResumeNext(throwable -> Completable.error(convertAPIError(throwable)));
    }


    private Throwable convertAPIError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return parseHttpException((HttpException) throwable);
        } else {
            return throwable;
        }
    }

    private Throwable parseHttpException(HttpException httpException){
        Response<?> response = httpException.response();
        if(response != null){
            try (ResponseBody errorBody = response.errorBody()){
                ApiErrorResponse apiErrorResponse = ObjectMapperProvider.getMapper().readValue(errorBody.string(), ApiErrorResponse.class);
                return new Throwable(apiErrorResponse.getMessage());
            } catch (Exception e) {
                return new Throwable("API ERROR but cannot read message");
            }
        }
        return new Throwable("API ERROR but no Response");
    }
}
