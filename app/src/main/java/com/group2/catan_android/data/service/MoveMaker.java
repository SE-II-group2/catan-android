package com.group2.catan_android.data.service;

import com.group2.catan_android.data.exception.IllegalGameMoveException;
import com.group2.catan_android.data.live.game.AccuseCheatingDto;
import com.group2.catan_android.data.live.game.BuildCityMoveDto;
import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.live.game.MoveRobberDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
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

    protected MoveMaker(Board board, Player localPlayer, List<Player> players) {
        disposable = new CompositeDisposable();
        this.board = board;
        this.localPlayer = localPlayer;
        this.players = players;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static MoveMaker getInstance() {
        if (moveMakerInstance == null) moveMakerInstance = new MoveMaker();
        return moveMakerInstance;
    }

    public void makeMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        if (gameMove instanceof MoveRobberDto) {
            makeMoveRobberMove((MoveRobberDto) gameMove);
            return;
        }
        if (gameMove instanceof AccuseCheatingDto) {
            if (isSetupPhase)
                throw new IllegalGameMoveException("Cant accuse someone of Cheating during the Setup Phase!");
            sendMove(gameMove);
            return;

        }
        if (players.get(0).getInGameID() != localPlayer.getInGameID()) {
            throw new IllegalGameMoveException("Not active player!");
        }
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto":
                makeRollDiceMove((RollDiceDto) gameMove);
                break;
            case "BuildRoadMoveDto":
                makeBuildRoadMove(gameMove);
                break;
            case "BuildVillageMoveDto":
                makeBuildVillageMove(gameMove);
                break;
            case "BuildCityMoveDto":
                makeBuildCityMove(gameMove);
                break;
            case "EndTurnMoveDto":
                makeEndTurnMove(gameMove);
                break;
            default:
                throw new IllegalGameMoveException("Unknown Dto format");
        }
    }

    private void makeMoveRobberMove(MoveRobberDto robberDto) throws IllegalGameMoveException {
        if (isSetupPhase) throw new IllegalGameMoveException("Cant move the Robber during the setup phase!");
        if (robberDto.isLegal() && players.get(0).getInGameID() != localPlayer.getInGameID())
            throw new IllegalGameMoveException("Not active player!");
        if (board.getHexagonList().get(robberDto.getHexagonID()).isHasRobber())
            throw new IllegalGameMoveException("Cant move the Robber to the same Hexagon it is currently in!");
        sendMove(robberDto);
    }

    private void makeEndTurnMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("End your turn during setup phase by placing a village and a road!");
        sendMove(gameMove);
        hasRolled = false;
    }

    private void makeBuildVillageMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        if (isSetupPhase && hasPlacedVillageInSetupPhase)
            throw new IllegalGameMoveException("Already placed a village during your turn!");
        if (!isSetupPhase && !localPlayer.resourcesSufficient(ResourceCost.VILLAGE.getCost()))
            throw new IllegalGameMoveException("Not enough resources to build a Village!");
        if (!board.addNewVillage(localPlayer, ((BuildVillageMoveDto) gameMove).getIntersectionID()))
            throw new IllegalGameMoveException("Can't build a Village here!");
        hasPlacedVillageInSetupPhase = true;
        sendMove(gameMove);
    }

    private void makeBuildRoadMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        if (isSetupPhase && !hasPlacedVillageInSetupPhase)
            throw new IllegalGameMoveException("Place a Village first during the setup phase!");
        if (!isSetupPhase && !localPlayer.resourcesSufficient(ResourceCost.ROAD.getCost()))
            throw new IllegalGameMoveException("Not enough resources to build a Road!");
        if (!board.addNewRoad(localPlayer, ((BuildRoadMoveDto) gameMove).getConnectionID()))
            throw new IllegalGameMoveException("Can't build a road here!");
        hasPlacedVillageInSetupPhase = false;
        sendMove(gameMove);
    }

    private void makeRollDiceMove(RollDiceDto gameMove) throws IllegalGameMoveException {
        if (hasRolled) throw new IllegalGameMoveException("Has already Rolled the dice this turn");
        if (gameMove.getDiceRoll() == 7 && gameMove.getMoveRobberDto() == null)
            throw new IllegalGameMoveException("Select field to move the Robber to!");

        sendMove(gameMove);
        hasRolled = true;
    }

    private void makeBuildCityMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("It is not possible to place cities during setup phase!");
        if (!localPlayer.resourcesSufficient(ResourceCost.CITY.getCost()))
            throw new IllegalGameMoveException("Not enough resources to build a City!");
        if (!board.addNewCity(localPlayer, ((BuildCityMoveDto) gameMove).getIntersectionID()))
            throw new IllegalGameMoveException("Can't build a city here!");
        sendMove(gameMove);
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
                .subscribe(activePlayer -> this.localPlayer = activePlayer);
        disposable.add(gameStateDisposable);
        disposable.add(activePlayerDisposable);
    }

    protected void sendMove(GameMoveDto gameMoveDto) {
        moveSenderRepository.sendMove(gameMoveDto, token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public boolean hasRolled() {
        return this.hasRolled;
    }

    public boolean isSetupPhase() {
        return isSetupPhase;
    }

    public void setHasRolled(boolean hasRolled) {
        this.hasRolled = hasRolled;
    }
}

