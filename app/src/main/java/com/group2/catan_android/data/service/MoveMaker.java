package com.group2.catan_android.data.service;

import android.util.Log;

import com.group2.catan_android.data.exception.IllegalGameMoveException;
import com.group2.catan_android.data.live.game.AcceptTradeOfferMoveDto;
import com.group2.catan_android.data.live.game.AccuseCheatingDto;
import com.group2.catan_android.data.live.game.BuildCityMoveDto;
import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.live.game.MakeTradeOfferMoveDto;
import com.group2.catan_android.data.live.game.UseProgressCardDto;
import com.group2.catan_android.data.live.game.MoveRobberDto;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.data.repository.moves.MoveSenderRepository;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.ResourceCost;

import javax.annotation.Nullable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoveMaker {
    private Board board;
    private Player localPlayer;
    private final MoveSenderRepository moveSenderRepository = MoveSenderRepository.getInstance();
    private boolean isSetupPhase = true;

    private final CurrentGamestateRepository currentGamestateRepository = CurrentGamestateRepository.getInstance();
    private final CompositeDisposable liveInDisposable;
    private final CompositeDisposable sendDisposable;
    private boolean hasRolled = false;
    private boolean hasPlacedVillageInSetupPhase = false;
    private String token;
    private static MoveMaker moveMakerInstance;
    private Player activePlayer;

    boolean isReceivingData;

    private MoveMaker() {
        board = new Board();
        liveInDisposable = new CompositeDisposable();
        sendDisposable = new CompositeDisposable();
    }

    public void reset(){
        clear();
        setupListeners();
        hasRolled = false;
        hasPlacedVillageInSetupPhase = false;
    }

    protected MoveMaker(Board board, Player localPlayer, Player activePlayer) {
        liveInDisposable = new CompositeDisposable();
        sendDisposable = new CompositeDisposable();
        this.board = board;
        this.localPlayer = localPlayer;
        this.activePlayer = activePlayer;
        isReceivingData = false;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static MoveMaker getInstance() {
        if (moveMakerInstance == null) moveMakerInstance = new MoveMaker();
        if(!moveMakerInstance.isReceivingData)
            moveMakerInstance.reset();
        return moveMakerInstance;
    }

    public void makeMove(GameMoveDto gameMove) throws IllegalGameMoveException{
        makeMove(gameMove, null);
    }
    public void makeMove(GameMoveDto gameMove, @Nullable ServerErrorCallback serverErrorCallback) throws IllegalGameMoveException {
        if (gameMove instanceof MoveRobberDto) {
            checkMoveRobberMove((MoveRobberDto) gameMove);
            sendMove(gameMove, serverErrorCallback);
            return;
        }
        if (gameMove instanceof AccuseCheatingDto) {
            if (isSetupPhase)
                throw new IllegalGameMoveException("Can't accuse someone of cheating during the Setup Phase!");
            sendMove(gameMove, serverErrorCallback);
            return;
        }
        if(gameMove instanceof AcceptTradeOfferMoveDto) {
            checkAcceptTradeOfferMove((AcceptTradeOfferMoveDto) gameMove);
            sendMove(gameMove, serverErrorCallback);
            return;
        }
        if (activePlayer.getInGameID() != localPlayer.getInGameID()) {
            throw new IllegalGameMoveException("Not active player!");
        }
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto":
                checkRollDiceMove();
                hasRolled = true;
                break;
            case "BuildRoadMoveDto":
                checkBuildRoadMove(gameMove);
                hasPlacedVillageInSetupPhase = false;
                break;
            case "BuildVillageMoveDto":
                checkBuildVillageMove(gameMove);
                hasPlacedVillageInSetupPhase = true;
                break;
            case "BuildCityMoveDto":
                checkBuildCityMove(gameMove);
                break;
            case "EndTurnMoveDto":
                checkEndTurnMove();
                hasRolled = false;
                break;
            case "BuyProgressCardDto":
                checkBuyProgressCardMove();
                break;
            case "UseProgressCardDto":
                checkUseProgressCardMove(gameMove);
                break;
            case "MakeTradeOfferMoveDto":
                checkMakeTradeOfferMove((MakeTradeOfferMoveDto) gameMove);
                break;
            default:
                throw new IllegalGameMoveException("Unknown Dto format");
        }
        sendMove(gameMove, serverErrorCallback);
    }

    private void checkAcceptTradeOfferMove(AcceptTradeOfferMoveDto gameMove)throws IllegalGameMoveException{
        if (isSetupPhase)
            throw new IllegalGameMoveException("Can't trade during setup phase");
        if(!localPlayer.resourcesSufficient(gameMove.getTradeOfferDto().getGiveResources()))
            throw new IllegalGameMoveException("Not enough Resources to accept the trade");
    }
    private void checkMakeTradeOfferMove(MakeTradeOfferMoveDto tradeMove) throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("Can't trade during setup phase");
        if (!localPlayer.resourcesSufficient(tradeMove.getGiveResources()))
            throw new IllegalGameMoveException("Not enough Resources");
        if (tradeMove.getToPlayers() == null)
            throw new IllegalGameMoveException("Something went wrong");
    }

    private void checkMoveRobberMove(MoveRobberDto robberDto) throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("Can't move the Robber during the setup phase!");
        if (robberDto.isLegal() && activePlayer.getInGameID() != localPlayer.getInGameID())
            throw new IllegalGameMoveException("Not active player!");
        if(!robberDto.isLegal() && activePlayer.getInGameID() == localPlayer.getInGameID())
            throw new IllegalGameMoveException("Cheating is only possible when you are not the active player!");
        if (board.getHexagonList().get(robberDto.getHexagonID()).isHavingRobber())
            throw new IllegalGameMoveException("Can't move the Robber to the same Hexagon it is currently in!");
    }

    private void checkEndTurnMove() throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("End your turn during setup phase by placing a village and a road!");
        if (!hasRolled)
            throw new IllegalGameMoveException("Dice needs to be rolled before you can end your turn!");
    }

    private void checkBuildVillageMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        if (isSetupPhase && hasPlacedVillageInSetupPhase)
            throw new IllegalGameMoveException("Already placed a village during your turn!");
        if (!isSetupPhase && !localPlayer.resourcesSufficient(ResourceCost.VILLAGE.getCost()))
            throw new IllegalGameMoveException("Not enough resources to build a Village!");
        if (!board.addNewVillage(localPlayer, ((BuildVillageMoveDto) gameMove).getIntersectionID()))
            throw new IllegalGameMoveException("Can't build a Village here!");
    }

    private void checkBuildRoadMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        if (isSetupPhase && !hasPlacedVillageInSetupPhase)
            throw new IllegalGameMoveException("Place a Village first during the setup phase!");
        if (!isSetupPhase && !localPlayer.resourcesSufficient(ResourceCost.ROAD.getCost()))
            throw new IllegalGameMoveException("Not enough resources to build a Road!");
        if (!board.addNewRoad(localPlayer, ((BuildRoadMoveDto) gameMove).getConnectionID()))
            throw new IllegalGameMoveException("Can't build a road here!");
    }

    private void checkRollDiceMove() throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("Can't roll the dice during setup phase");
        if (hasRolled) throw new IllegalGameMoveException("Has already Rolled the dice this turn");
    }

    private void checkBuyProgressCardMove() throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("Can't buy progress cards during setup phase!");
        if (!localPlayer.resourcesSufficient(ResourceCost.PROGRESS_CARD.getCost())){
            throw new IllegalGameMoveException("Not enough resources!");
        }
    }

    private void checkUseProgressCardMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        UseProgressCardDto useProgressCardDto = (UseProgressCardDto) gameMove;
        if (isSetupPhase) {
            throw new IllegalGameMoveException("Can't use progress-card during setup phase");
        }
        if (!localPlayer.getProgressCards().contains(useProgressCardDto.getProgressCardType())) {
            throw new IllegalGameMoveException("Card type not in possession");
        }
    }
    private void checkBuildCityMove(GameMoveDto gameMove) throws IllegalGameMoveException {
        if (isSetupPhase)
            throw new IllegalGameMoveException("It is not possible to place cities during setup phase!");
        if (!localPlayer.resourcesSufficient(ResourceCost.CITY.getCost()))
            throw new IllegalGameMoveException("Not enough resources to build a City!");
        if (!board.addNewCity(localPlayer, ((BuildCityMoveDto) gameMove).getIntersectionID()))
            throw new IllegalGameMoveException("Can't build a city here!");
    }

    void setupListeners() {
        Disposable gameStateDisposable = currentGamestateRepository.getCurrentGameStateObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(currentGameState -> {
                    this.board = currentGameState.getBoard();
                    this.isSetupPhase = board.isSetupPhase();
                    this.activePlayer = currentGameState.getActivePlayer();
                });
        Disposable localPlayerDisposable = currentGamestateRepository.getCurrentLocalPlayerObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(player -> localPlayer = player);

        liveInDisposable.add(gameStateDisposable);
        liveInDisposable.add(localPlayerDisposable);
        isReceivingData = true;
    }

    protected void sendMove(GameMoveDto gameMoveDto, ServerErrorCallback serverErrorCallback) {
        Disposable d =  moveSenderRepository.sendMove(gameMoveDto, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()->{},
                        throwable -> {
                            if(serverErrorCallback != null){
                                serverErrorCallback.onServerError(throwable);
                            }else{
                                Log.e("GameServer", "Unhandled Server error", throwable.getCause());
                            }
                        });
        sendDisposable.add(d);
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

    public void clear(){
        liveInDisposable.clear();
        sendDisposable.clear();
        isReceivingData = false;
    }
}

