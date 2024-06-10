package com.group2.catan_android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.gamelogic.Player;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivePlayerViewModel extends ViewModel {
    private final CurrentGamestateRepository datasource;

    private final MutableLiveData<Player> playerMutableLiveData;

    CompositeDisposable disposable;

    public ActivePlayerViewModel(CurrentGamestateRepository datasource) {
        this.datasource = datasource;
        this.playerMutableLiveData = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        setupListeners();
    }
    public MutableLiveData<Player> getPlayerMutableLiveData() {
        return playerMutableLiveData;
    }
    private void setupListeners() {
        Disposable playerDisposable = datasource.getCurrentLocalPlayerObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        playerMutableLiveData::setValue);
        disposable.add(playerDisposable);
    }

    public static final ViewModelInitializer<ActivePlayerViewModel> initializer = new ViewModelInitializer<>(
            ActivePlayerViewModel.class,
            creationExtras -> new ActivePlayerViewModel(CurrentGamestateRepository.getInstance())
    );
}
