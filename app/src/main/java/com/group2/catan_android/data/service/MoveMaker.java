package com.group2.catan_android.data.service;

import android.util.Log;

import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.data.repository.moves.MoveSenderRepository;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.ResourceCost;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoveMaker {
    private Board board;
    private Player localPlayer;
    private final MoveSenderRepository moveSenderRepository = MoveSenderRepository.getInstance();
    private List<Player> players;
    private boolean isSetupPhase = true;
    private final CurrentGamestateRepository currentGamestateRepository = CurrentGamestateRepository.getInstance();
    private final CompositeDisposable disposable;
    private boolean hasRolled = false;
    private boolean hasPlacedVillageInSetupPhase = false;
    private String token;
    private static MoveMaker moveMakerInstance;

    private MoveMaker() {
        board = new Board();
        disposable = new CompositeDisposable();
        setupListeners();
    }

    protected MoveMaker(Board board, Player localPlayer, List<Player> players){
        disposable = new CompositeDisposable();
        this.board=board;
        this.localPlayer=localPlayer;
        this.players=players;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static MoveMaker getInstance() {
        if (moveMakerInstance == null) moveMakerInstance = new MoveMaker();
        return moveMakerInstance;
    }

    public void makeMove(GameMoveDto gameMove) throws Exception {
        if (players.get(0).getInGameID() != localPlayer.getInGameID()) {
            throw new Exception("Not active player!");
        }
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto":
                makeRollDiceMove(gameMove);
                break;
            case "BuildRoadMoveDto":
                makeBuildRoadMove(gameMove);
                break;
            case "BuildVillageMoveDto":
                makeBuildVillageMove(gameMove);
                break;
            case "EndTurnMoveDto":
                makeEndTurnMove(gameMove);
                break;
            default:
                throw new Exception("Unknown Dto format");
        }
    }

    private void makeEndTurnMove(GameMoveDto gameMove) throws Exception {
        if (isSetupPhase)
            throw new Exception("End your turn during setupphase by placing a village and a road");
        sendMove(gameMove);
        hasRolled = false;
    }

    private void makeBuildVillageMove(GameMoveDto gameMove) throws Exception {
        if(isSetupPhase && hasPlacedVillageInSetupPhase)
            throw new Exception("Already Placed a Village during your turn!");
        if (!isSetupPhase && !localPlayer.resourcesSufficient(ResourceCost.VILLAGE.getCost()))
            throw new Exception("Not enough Resources");
        if (!board.addNewVillage(localPlayer, ((BuildVillageMoveDto) gameMove).getIntersectionID()))
            throw new Exception("Cant build a Village here");
        hasPlacedVillageInSetupPhase = true;
        sendMove(gameMove);
    }

    private void makeBuildRoadMove(GameMoveDto gameMove) throws Exception {
        if (isSetupPhase && !hasPlacedVillageInSetupPhase)
            throw new Exception("Place a Village first during the setup phase!");
        if (!isSetupPhase && !localPlayer.resourcesSufficient(ResourceCost.ROAD.getCost()))
            throw new Exception("Not enough resources!");
        if (!board.addNewRoad(localPlayer, ((BuildRoadMoveDto) gameMove).getConnectionID()))
            throw new Exception("Invalid place to build a road!");
        hasPlacedVillageInSetupPhase=false;
        sendMove(gameMove);
    }

    private void makeRollDiceMove(GameMoveDto gameMove) throws Exception {
        if (hasRolled) throw new Exception("Has already Rolled the dice this turn");
        sendMove(gameMove);
        hasRolled = true;
    }

    void setupListeners() {
        Disposable gameStateDisposable = currentGamestateRepository.getCurrentGameStateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentGameState -> {
                    this.board = currentGameState.getBoard();
                    this.players = currentGameState.getPlayers();
                    this.isSetupPhase = board.isSetupPhase();
                });
        Disposable activePlayerDisposable = currentGamestateRepository.getCurrentLocalPlayerObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(activePlayer -> {
                    this.localPlayer = activePlayer;
                });
        disposable.add(gameStateDisposable);
        disposable.add(activePlayerDisposable);
    }

    private void sendMove(GameMoveDto gameMoveDto) {
        moveSenderRepository.sendMove(gameMoveDto, token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public boolean hasRolled(){
        return this.hasRolled;
    }

    public boolean isSetupPhase(){
        return isSetupPhase;
    }
}

