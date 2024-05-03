package com.group2.catan_android.data.service;

import com.group2.catan_android.data.api.JoinGameRequest;

import io.reactivex.Completable;

public interface GameJoiner {
    Completable joinGame(JoinGameRequest request);
    Completable createGame(JoinGameRequest request);
}
