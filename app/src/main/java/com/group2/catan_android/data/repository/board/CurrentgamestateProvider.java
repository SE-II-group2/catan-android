package com.group2.catan_android.data.repository.board;

import io.reactivex.Observable;

import com.group2.catan_android.gamelogic.CurrentGameState;
import com.group2.catan_android.gamelogic.Player;

import java.util.List;

public interface CurrentgamestateProvider {
    Observable<CurrentGameState> getCurrentGameStateObservable();

    Observable<Player> getCurrentActivePlayerObservable();

    Observable<List<Player>>getAllPlayerObservable();
}
