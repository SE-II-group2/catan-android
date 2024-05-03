package com.group2.catan_android.data.repository.lobby;

import com.group2.catan_android.data.api.JoinGameRequest;
import com.group2.catan_android.data.api.JoinGameResponse;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface LobbyJoiner {
    Single<JoinGameResponse> joinGame(JoinGameRequest joinRequest);
    Single<JoinGameResponse> createGame(JoinGameRequest joinGameRequest);
    Completable leaveGame(String token);
}
