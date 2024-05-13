package com.group2.catan_android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.gamelogic.Player;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PlayerListViewModel extends ViewModel {
    private final CurrentGamestateRepository datasource;

    private final MutableLiveData<List<Player>> playerMutableLiveData;

    CompositeDisposable disposable;

    public PlayerListViewModel(CurrentGamestateRepository datasource) {
        this.datasource = datasource;
        this.playerMutableLiveData = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        setupListeners();
    }
    public MutableLiveData<List<Player>> getPlayerMutableLiveData() {
        return playerMutableLiveData;
    }
    private void setupListeners() {
        Disposable playerDisposable = datasource.getAllPlayerObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playerMutableLiveData::setValue);
        disposable.add(playerDisposable);
    }

    public static final ViewModelInitializer<PlayerListViewModel> initializer = new ViewModelInitializer<>(
            PlayerListViewModel.class,
            creationExtras -> new PlayerListViewModel(CurrentGamestateRepository.getInstance())
    );
}
