package com.group2.catan_android.gamelogic;

import android.util.Log;

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
    private static MoveMaker moveMakerInstance;

    private MoveMaker() {
        board = new Board();
        token = TokenRepository.getInstance().getToken();
        currentIngameID = TokenRepository.getInstance().getInGameID();
        disposable = new CompositeDisposable();
        setupListeners();
    }

    public static MoveMaker getInstance() {
        if (moveMakerInstance == null) moveMakerInstance = new MoveMaker();
        return moveMakerInstance;
    }

    // fixme networking (dto switch-case, send) and game logic (addnewroad, hasrolled) are borderline intertwined
    public void makeMove(GameMoveDto gameMove) throws Exception {
        if (players.get(0).getInGameID() != activePlayer.getInGameID()) {
            throw new Exception("Not active player!");
        }
        Log.d("active player: ", "active player id: "+activePlayer.getInGameID()+", players[0] id: "+players.get(0).getInGameID());
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto":
                if (hasRolled) throw new Exception("Has already Rolled the dice this turn");
                sendMove(gameMove);
                hasRolled = true;
                break;
            case "BuildRoadMoveDto":
                if (isSetupPhase && hasPlacedVillageInSetupPhase) {
                    sendMove(gameMove);
                } else {
                    if (!activePlayer.resourcesSufficient(ResourceCost.ROAD.getCost()))
                        throw new Exception("Not enough resources");
                    if (!board.addNewRoad(activePlayer, ((BuildRoadMoveDto) gameMove).getConnectionID()))
                        throw new Exception("invalid place to build a road!");
                    sendMove(gameMove);
                }
                break;
            case "BuildVillageMoveDto":
                if (!isSetupPhase && !activePlayer.resourcesSufficient(ResourceCost.VILLAGE.getCost()))
                    throw new Exception("Not enough Resources");
                if (!board.addNewVillage(activePlayer, ((BuildVillageMoveDto) gameMove).getIntersectionID())) {
                    Log.d("else", "getIntersectinID: " + ((BuildVillageMoveDto) gameMove).getIntersectionID());
                    throw new Exception("Cant build a Village here");
                }
                hasPlacedVillageInSetupPhase = true;
                sendMove(gameMove);
                break;
            case "EndTurnMoveDto":
                if (isSetupPhase)
                    throw new Exception("End your turn during setupphase by placing a village and a road");
                sendMove(gameMove);
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

    private void sendMove(GameMoveDto gameMoveDto) throws Exception {

        moveSenderRepository.sendMove(gameMoveDto, token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

    }

}

