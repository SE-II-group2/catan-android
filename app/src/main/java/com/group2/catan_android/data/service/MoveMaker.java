package com.group2.catan_android.data.service;

import com.group2.catan_android.data.exception.IllegalGameMoveException;
import com.group2.catan_android.data.live.game.AccuseCheatingDto;
import com.group2.catan_android.data.live.game.BuildCityMoveDto;
import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.live.game.UseProgressCardDto;
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
    private Player activePlayer;

    private MoveMaker() {
        board = new Board();
        disposable = new CompositeDisposable();
        setupListeners();
    }

    protected MoveMaker(Board board, Player localPlayer, List<Player> players, Player activePlayer) {
        disposable = new CompositeDisposable();
        this.board = board;
        this.localPlayer = localPlayer;
        this.players = players;
        this.activePlayer = activePlayer;
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
        if (activePlayer.getInGameID() != localPlayer.getInGameID()) {
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
            case "BuyProgressCardDto":
                makeBuyProgressCardMove(gameMove);
                break;
            case "UseProgressCardDto":
                makeUseProgressCardMove(gameMove);
                break;
            default:
                throw new IllegalGameMoveException("Unknown Dto format");
        }
    }

    private void makeMoveRobberMove(MoveRobberDto robberDto) throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("Cant move the Robber during the setup phase!");
        if (robberDto.isLegal() && activePlayer.getInGameID() != localPlayer.getInGameID())
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
        if (isSetupPhase)
            throw new IllegalGameMoveException("Cant roll the Dice during SetupPhase");
        if (hasRolled) throw new IllegalGameMoveException("Has already Rolled the dice this turn");
        sendMove(gameMove);
        hasRolled = true;
    }

    private void makeBuyProgressCardMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("Can't buy progress-card during setup phase");
        if (!localPlayer.resourcesSufficient(ResourceCost.PROGRESS_CARD.getCost())){
            throw new IllegalGameMoveException("Not enough resources");
        }
        localPlayer.adjustResources(ResourceCost.PROGRESS_CARD.getCost());
        sendMove(gameMove);
    }

    private void makeUseProgressCardMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        UseProgressCardDto useProgressCardDto = (UseProgressCardDto) gameMove;
        if (isSetupPhase) {
            throw new IllegalGameMoveException("Can't use progress-card during setup phase");
        }
        if (!localPlayer.getProgressCards().contains(useProgressCardDto.getProgressCardType())) {
            throw new IllegalGameMoveException("Card type not in possession");
        }
        localPlayer.removeProgressCard(useProgressCardDto.getProgressCardType());
        sendMove(gameMove);
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
                .subscribe(currentGameState -> {
                    this.board = currentGameState.getBoard();
                    this.players = currentGameState.getPlayers();
                    this.isSetupPhase = board.isSetupPhase();
                    this.activePlayer = currentGameState.getActivePlayer();
                });
        Disposable localPlayerDisposable = currentGamestateRepository.getCurrentLocalPlayerObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(localPlayer -> this.localPlayer = localPlayer);

        disposable.add(gameStateDisposable);
        disposable.add(localPlayerDisposable);
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

