package com.group2.catan_android.data.repository.gameprogress;

import android.util.Log;

import com.group2.catan_android.data.live.game.CurrentGameStateDto;
import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.repository.LiveDataReceiver;
import com.group2.catan_android.gamelogic.CurrentGameState;
import com.group2.catan_android.gamelogic.Player;

import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class GameProgressRepository implements GameProgressProvider, LiveDataReceiver<GameProgressDto> {

    BehaviorSubject <GameProgressDto> gameProgressDtoBehaviorSubject;
    private Flowable<GameProgressDto> liveDataIn;
    private static GameProgressRepository instance;
    Disposable d;

    private GameProgressRepository(){
        this.gameProgressDtoBehaviorSubject = BehaviorSubject.create();

    }

    public static GameProgressRepository getInstance(){
        if(instance==null)instance=new GameProgressRepository();
        return instance;
    }
    @Override
    public void setLiveData(Flowable<GameProgressDto> in) {
        this.liveDataIn=in;
        cleanup();
        wireDataSources();
    }


    private void cleanup() {
        this.gameProgressDtoBehaviorSubject.onNext(new GameProgressDto());
        if (d != null)
            d.dispose();
    }

    private void wireDataSources() {
        d = liveDataIn
                .doOnComplete(this::cleanup)
                .subscribe(GameProgressDto -> gameProgressDtoBehaviorSubject.onNext(GameProgressDto));
    }

    @Override
    public Observable<GameProgressDto> getGameProgressObservable() {
        return gameProgressDtoBehaviorSubject;
    }
}
