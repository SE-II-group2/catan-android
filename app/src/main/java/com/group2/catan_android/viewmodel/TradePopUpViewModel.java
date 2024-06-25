package com.group2.catan_android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.model.SelectablePlayer;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.gamelogic.Player;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class TradePopUpViewModel extends ViewModel {
    private final List<SelectablePlayer> selectablePlayers;
    private List<Player> players;
    private final MutableLiveData<List<SelectablePlayer>> selectablePlayerMutableLiveData;
    private final CurrentGamestateRepository currentGamestateRepository;
    private final CompositeDisposable disposable;
    private Player localPlayer;


    private TradePopUpViewModel(CurrentGamestateRepository currentGamestateRepository){
        this.currentGamestateRepository=currentGamestateRepository;
        selectablePlayerMutableLiveData = new MutableLiveData<>();
        selectablePlayers = new ArrayList<>();
        disposable = new CompositeDisposable();
        setUpListeners();
    }

    public void togglePlayer(int index){
        selectablePlayers.get(index).setSelected(!selectablePlayers.get(index).isSelected());
        updateSelectedPlayers();
    }

    public MutableLiveData<List<SelectablePlayer>> getSelectablePlayerMutableLiveData() {
        return selectablePlayerMutableLiveData;
    }

    private void setUpListeners(){
        Disposable localPlayerDisposable = currentGamestateRepository.getCurrentLocalPlayerObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(this::updateLocalPlayer);
        Disposable allPlayerDisposable = currentGamestateRepository.getAllPlayerObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(this::updatePlayers);
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

    public void selectAll(){
        for(SelectablePlayer s : selectablePlayers)
            s.setSelected(true);
        updateSelectedPlayers();
    }
    public void deselectAll(){
        for(SelectablePlayer s : selectablePlayers)
            s.setSelected(false);
        updateSelectedPlayers();
    }

    public List<Integer> getSelectedPlayerIds(){
        ArrayList<Integer> result = new ArrayList<>();
        selectablePlayers.stream().filter(SelectablePlayer::isSelected).forEach(selectablePlayer -> {
            result.add(selectablePlayer.getPlayer().getInGameID());
        });
        return result;
    }
    private void updateSelectedPlayers(){
        if(players == null || localPlayer == null) return;
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
        selectablePlayerMutableLiveData.setValue(selectablePlayers);
    }

    public static final ViewModelInitializer<TradePopUpViewModel> initializer = new ViewModelInitializer<>(
            TradePopUpViewModel.class,
            creationExtras -> new TradePopUpViewModel(CurrentGamestateRepository.getInstance())
    );

    private SelectablePlayer getSelectablePlayerByID(int playerID){
        for(SelectablePlayer p : selectablePlayers){
            if(p.getPlayer().getInGameID()==playerID){
                return p;
            }
        }
        return null;
    }

}
