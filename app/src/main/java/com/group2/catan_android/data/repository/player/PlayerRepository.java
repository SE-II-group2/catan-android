package com.group2.catan_android.data.repository.player;

import com.group2.catan_android.data.repository.LiveDataReceiver;
import com.group2.catan_android.data.live.PlayerDto;
import com.group2.catan_android.data.live.PlayersInLobbyDto;
import com.group2.catan_android.data.model.DisplayablePlayer;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class PlayerRepository implements LiveDataReceiver<PlayersInLobbyDto>, AdminPlayerProvider {
    private static PlayerRepository instance;
    private PlayerRepository(){
        this.playerSubject = BehaviorSubject.createDefault(new ArrayList<>());
        this.adminSubject = BehaviorSubject.createDefault(false);
        this.players = new ArrayList<>();
    }
    public static PlayerRepository getInstance(){
        if(instance == null){
            instance = new PlayerRepository();
        }
        return instance;
    }
    private final List<DisplayablePlayer> players;
    private final BehaviorSubject<List<DisplayablePlayer>> playerSubject;
    private final BehaviorSubject<Boolean> adminSubject;
    private Flowable<PlayersInLobbyDto> liveDataIn;
    private int currentPlayerID = -1; //The id of the Player who is playing the game;
    private boolean playerIsAdmin;
    Disposable d;
    @Override
    public void setLiveData(Flowable<PlayersInLobbyDto> liveDataIn){
        this.liveDataIn = liveDataIn;
        cleanup();
        wireDataSources();
    }

    private void cleanup(){
        adminSubject.onNext(false);
        players.clear();
        playerSubject.onNext(new ArrayList<>(players));
        if(d != null)
            d.dispose();
    }

    private void wireDataSources(){
        d = liveDataIn
                .doOnComplete(this::cleanup)
                .subscribe(playersInLobbyDto -> {
            List<PlayerDto> serverPlayers = playersInLobbyDto.getPlayers();
            addAllPlayers(serverPlayers);
            PlayerDto admin = playersInLobbyDto.getAdmin();
            handleAdmin(admin);
            playerSubject.onNext(new ArrayList<>(players));
        });
    }

    private void addAllPlayers(List<PlayerDto> serverPlayers){
        players.clear();
        serverPlayers.forEach(playerDto -> {
            DisplayablePlayer p = new DisplayablePlayer(false, playerDto.getInGameID(), playerDto.getDisplayName(), playerDto.getState());
            players.add(p);
        });
    }
    private void handleAdmin(PlayerDto serverAdmin){
        DisplayablePlayer admin = getPlayerWithID(serverAdmin.getInGameID());
        if(admin != null)
            admin.setAdmin(true);
        adminSubject.onNext(currentPlayerID == serverAdmin.getInGameID());
    }

    private DisplayablePlayer getPlayerWithID(int inGameID){
        for(DisplayablePlayer p : players) {
            if (p.getInGameID() == inGameID)
                return p;
        }
        return null;
    }

    public void setCurrentPlayerID(int playerID){
        this.currentPlayerID = playerID;
    }

    @Override
    public Observable<Boolean> getAdminObservable(){return adminSubject;}
    @Override
    public Observable<List<DisplayablePlayer>> getPlayerObservable(){
        return playerSubject;
    }
}
