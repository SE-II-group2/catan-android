package com.group2.catan_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.model.SelectablePlayer;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.gamelogic.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class TradePopUpViewModel extends ViewModel {
    public static final int WHEAT_INDEX = 0;
    public static final int SHEEP_INDEX = 1;
    public static final int WOOD_INDEX = 2;
    public static final int BRICK_INDEX = 3;
    public static final int STONE_INDEX = 4;
    public static final int RESOURCE_LENGTH = 5;



    private List<SelectablePlayer> selectablePlayers;
    private List<Player> players;
    private final MutableLiveData<List<SelectablePlayer>> selectablePlayerMutableLiveData;
    private final MutableLiveData<int[]> giveResourceMutableLiveData;
    private final MutableLiveData<int[]> getResourceMutableLiveData;
    private int[] giveResources;
    private int[] getResources;
    private CurrentGamestateRepository currentGamestateRepository;
    private CompositeDisposable disposable;
    private Player localPlayer;


    private TradePopUpViewModel(CurrentGamestateRepository currentGamestateRepository){
        this.currentGamestateRepository=currentGamestateRepository;
        giveResources = new int[RESOURCE_LENGTH];
        getResources = new int[RESOURCE_LENGTH];
        selectablePlayerMutableLiveData = new MutableLiveData<>();
        giveResourceMutableLiveData = new MutableLiveData<>();
        getResourceMutableLiveData = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        setUpListeners();
    }

    public void increaseGiveResource(int resourceID) throws IllegalStateException{
        giveResources[resourceID]++;
    }
    public void increaseGetResource(int resourceID) throws IllegalStateException{
        getResources[resourceID]++;
    }
    public void decreaseGiveResource(int resourceID) throws IllegalStateException{
        giveResources[resourceID]--;
    }
    public void decreaseGetResource(int resourceID) throws IllegalStateException{
        getResources[resourceID]--;
    }
    public void togglePlayer(int index){
        selectablePlayers.get(index).setSelected(!selectablePlayers.get(index).isSelected());
    }

    public MutableLiveData<List<SelectablePlayer>> getSelectablePlayerMutableLiveData() {
        return selectablePlayerMutableLiveData;
    }

    private void setUpListeners(){
        Disposable localPlayerDisposable = currentGamestateRepository.getCurrentLocalPlayerObservable().subscribe(this::updateLocalPlayer);
        Disposable allPlayerDisposable = currentGamestateRepository.getAllPlayerObservable().subscribe(this::updatePlayers);
        disposable.add(localPlayerDisposable);
        disposable.add(allPlayerDisposable);
    }

    private void updatePlayers(List<Player> players){
        this.players = players;
        if(localPlayer==null){
            return;
        }
        updateSelectedPlayers();
    }

    private void updateLocalPlayer(Player localPlayer){
        this.localPlayer = localPlayer;
        if(players==null){
            return;
        }
        updateSelectedPlayers();
    }
    private void updateSelectedPlayers(){
        for(Player p : players){
            if(p.getInGameID()==localPlayer.getInGameID()){
                continue;
            }
            SelectablePlayer s = getSelectablePlayerByID(p.getInGameID());
            if(s==null){
                selectablePlayers.add(new SelectablePlayer(p, true));

            }else{
                s.setPlayer(p);
            }
        }
    }

    public static final ViewModelInitializer<TradePopUpViewModel> initializer = new ViewModelInitializer<>(
            TradePopUpViewModel.class,
            creationExtras -> new TradePopUpViewModel(CurrentGamestateRepository.getInstance())
    );

    private Player getPlayerByID(int playerID){
        for(Player p : players){
            if(p.getInGameID()==playerID){
                return p;
            }
        }
        return null;
    }

    private SelectablePlayer getSelectablePlayerByID(int playerID){
        for(SelectablePlayer p : selectablePlayers){
            if(p.getPlayer().getInGameID()==playerID){
                return p;
            }
        }
        return null;
    }

}
