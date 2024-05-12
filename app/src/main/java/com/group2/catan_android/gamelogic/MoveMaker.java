package com.group2.catan_android.gamelogic;

import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.model.DisplayablePlayer;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.data.repository.moves.MoveSenderRepository;
import com.group2.catan_android.data.repository.token.TokenRepository;
import com.group2.catan_android.gamelogic.enums.ResourceCost;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoveMaker {
    private Board board;
    private Player activePlayer;
    private MoveSenderRepository moveSenderRepository = MoveSenderRepository.getInstance();
    private List<Player> players;
    private boolean isSetupPhase = true;
    private static final int VICTORYPOINTSFORVICTORY = 10;
    private boolean gameover = false;
    private CurrentGamestateRepository currentGamestateRepository = CurrentGamestateRepository.getInstance();
    private CompositeDisposable disposable;
    private boolean hasRolled = false;
    private boolean hasPlacedVillageInSetupPhase = false;
    private int currentIngameID;
    private String token;

    public MoveMaker() {
        board = new Board();
        token = TokenRepository.getInstance().getToken();
        currentIngameID = TokenRepository.getInstance().getInGameID();
        disposable = new CompositeDisposable();
        setupListeners();
    }

    public void makeMove(GameMoveDto gameMove) throws Exception {
        if (players.get(0) != activePlayer) throw new Exception("Not active player!");
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto":
                if (hasRolled) throw new Exception("Has already Rolled the dice this turn");
                moveSenderRepository.sendMove(gameMove, token);
                hasRolled = true;
                break;
            case "BuildRoadMoveDto":
                if (isSetupPhase && hasPlacedVillageInSetupPhase)
                    moveSenderRepository.sendMove(gameMove, token);
                if (!activePlayer.resourcesSufficient(ResourceCost.ROAD.getCost()))
                    throw new Exception("Not enough resources");
                if (!board.addNewRoad(activePlayer, ((BuildRoadMoveDto) gameMove).getConnectionID()))
                    throw new Exception("invalid place to build a road!");
                moveSenderRepository.sendMove(gameMove, token);
                break;
            case "BuildVillageMoveDto":
                if (!isSetupPhase && activePlayer.resourcesSufficient(ResourceCost.VILLAGE.getCost()))throw new Exception("Not enough Resources");
                if (!board.addNewVillage(activePlayer, ((BuildVillageMoveDto) gameMove).getIntersectionID()))
                    throw new Exception("Cant build a road here");
                moveSenderRepository.sendMove(gameMove,token);
                break;
            case "EndTurnMoveDto":
                if(isSetupPhase)throw new Exception("End your turn during setupphase by placing a village and a road");
                moveSenderRepository.sendMove(gameMove,token);
                hasRolled = false;
                break;
            default:
                throw new Exception("Unknown Dto format");
        }
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
        Disposable activePlayerDisposable = currentGamestateRepository.getCurrentActivePlayerObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(activePlayer -> {
                    this.activePlayer = activePlayer;
                });
        disposable.add(gameStateDisposable);
        disposable.add(activePlayerDisposable);
    }

}

