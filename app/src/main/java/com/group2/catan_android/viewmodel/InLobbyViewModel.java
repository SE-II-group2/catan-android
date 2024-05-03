package com.group2.catan_android.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.model.DisplayablePlayer;
import com.group2.catan_android.data.repository.player.AdminPlayerProvider;
import com.group2.catan_android.data.repository.player.PlayerRepository;
import com.group2.catan_android.data.repository.token.TokenRepository;
import com.group2.catan_android.data.service.GameController;
import com.group2.catan_android.data.service.GameLeaver;

import java.util.Collection;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class InLobbyViewModel extends ViewModel {
  private final GameLeaver gameLeaver;
  private final AdminPlayerProvider datasource;
  private final TokenRepository tokenRepository;
  private final MutableLiveData<List<DisplayablePlayer>> playersLiveData;
  private final MutableLiveData<Boolean> playerIsAdmin;


  CompositeDisposable disposable;
  public InLobbyViewModel(GameLeaver gameLeaver, AdminPlayerProvider datasource, TokenRepository tokenRepository){
    this.gameLeaver = gameLeaver;
    this.datasource = datasource;
    this.tokenRepository = tokenRepository;
    disposable = new CompositeDisposable();
    playersLiveData = new MutableLiveData<>();
    playerIsAdmin = new MutableLiveData<>();
    setupListeners();
  }

  public String getGameID(){
    return tokenRepository.getGameID();
  }

  public MutableLiveData<List<DisplayablePlayer>> getPlayersLiveData(){
    return playersLiveData;
  }

  public MutableLiveData<Boolean> getPlayerIsAdminLiveData(){
    return playerIsAdmin;
  }

  void setupListeners(){
    Disposable playerDisposable = datasource.getPlayerObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(playersLiveData::setValue);
    Disposable adminDisposable = datasource.getAdminObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(playerIsAdmin::setValue);
    disposable.add(playerDisposable);
    disposable.add(adminDisposable);
  }

  public Completable leaveGame(){
    return gameLeaver.leaveGame();
  }


  @Override
  public void onCleared(){
    disposable.dispose();
  }
  public static final ViewModelInitializer<InLobbyViewModel> initializer = new ViewModelInitializer<>(
          InLobbyViewModel.class,
                          creationExtras -> new InLobbyViewModel(GameController.getInstance(), PlayerRepository.getInstance(), TokenRepository.getInstance())
  );
}
