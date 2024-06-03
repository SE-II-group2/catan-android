package com.group2.catan_android.data.repository.gameprogress;

import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.live.game.RollDiceDto;

import io.reactivex.Observable;

public interface GameProgressProvider {
    Observable<GameProgressDto> getGameProgressObservable();
    Observable<RollDiceDto> getRollDiceObservable();
    Observable<EndTurnMoveDto> getEndTurnMoveObservable();
}
