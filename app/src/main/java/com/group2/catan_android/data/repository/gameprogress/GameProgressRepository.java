package com.group2.catan_android.data.repository.gameprogress;

import android.util.Log;

import com.group2.catan_android.data.live.game.CurrentGameStateDto;
import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
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
    BehaviorSubject <RollDiceDto> rollDiceDtoBehaviorSubject;
    BehaviorSubject <EndTurnMoveDto> endTurnMoveDtoBehaviorSubject;
    private Flowable<GameProgressDto> liveDataIn;
    private static GameProgressRepository instance;
    Disposable d;

    private GameProgressRepository(){
        this.gameProgressDtoBehaviorSubject = BehaviorSubject.create();
        this.rollDiceDtoBehaviorSubject = BehaviorSubject.create();
        this.endTurnMoveDtoBehaviorSubject = BehaviorSubject.create();
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
        this.rollDiceDtoBehaviorSubject.onNext(new RollDiceDto());
        this.endTurnMoveDtoBehaviorSubject.onNext(new EndTurnMoveDto());
        if (d != null)
            d.dispose();
    }

    private void wireDataSources() {
        d = liveDataIn
                .doOnComplete(this::cleanup)
                .subscribe(GameProgressDto -> {
                    switch (GameProgressDto.getGameMoveDto().getClass().getSimpleName()){
                        case "RollDiceDto":
                            rollDiceDtoBehaviorSubject.onNext((RollDiceDto) GameProgressDto.getGameMoveDto());
                            break;
                        case "EndTurnMoveDto":
                            endTurnMoveDtoBehaviorSubject.onNext((EndTurnMoveDto) GameProgressDto.getGameMoveDto());
                            break;
                        default:
                            //Log.d("GameProgressRepository UnknownDtoFormat", "unkown DTO format");
                            break;
                    }
                    gameProgressDtoBehaviorSubject.onNext(GameProgressDto);
                });
    }


    @Override
    public Observable<GameProgressDto> getGameProgressObservable() {
        return gameProgressDtoBehaviorSubject;
    }

    @Override
    public Observable<RollDiceDto> getRollDiceObservable() {
        return rollDiceDtoBehaviorSubject;
    }

    @Override
    public Observable<EndTurnMoveDto> getEndTurnMoveObservable() {
        return endTurnMoveDtoBehaviorSubject;
    }
}
