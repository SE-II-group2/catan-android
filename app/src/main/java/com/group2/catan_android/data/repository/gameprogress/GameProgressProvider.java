package com.group2.catan_android.data.repository.gameprogress;

import com.group2.catan_android.data.live.game.GameProgressDto;

import io.reactivex.Observable;

public interface GameProgressProvider {
    Observable<GameProgressDto> getGameProgressObservable();
}
