package com.group2.catan_android.data.repository.moves;

import com.group2.catan_android.data.live.game.GameMoveDto;

import io.reactivex.Completable;

public interface MoveSender {
    Completable sendMove(GameMoveDto gameMoveDto, String token);
}
