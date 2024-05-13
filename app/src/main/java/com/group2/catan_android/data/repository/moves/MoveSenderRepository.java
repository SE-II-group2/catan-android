package com.group2.catan_android.data.repository.moves;

import com.group2.catan_android.data.api.ApiErrorResponse;
import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.service.GameApi;
import com.group2.catan_android.data.util.ObjectMapperProvider;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Completable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class MoveSenderRepository implements MoveSender{

    private static MoveSenderRepository instance;
    private final GameApi api;

    public MoveSenderRepository(GameApi api) {
        this.api = api;
    }
    @NotNull
    synchronized public static MoveSenderRepository getInstance(){
        return instance;
    }

    public static void initialize(@NotNull GameApi api){
        if(instance==null){
            instance = new MoveSenderRepository(api);
        }
    }

    @Override
    public Completable sendMove(GameMoveDto gameMoveDto, String token) {
        return api.makeMove(token, gameMoveDto)
              .onErrorResumeNext(throwable -> Completable.error(convertAPIError(throwable)));
    }

    //HERE FOR THE MOMENT, NOT FINAL DESTINATION
    public Completable startGame(String token){
        return api.startGame(token).onErrorResumeNext(throwable -> Completable.error(convertAPIError(throwable)));
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
