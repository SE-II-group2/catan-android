package com.group2.catan_android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.repository.gameprogress.GameProgressRepository;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.gamelogic.Board;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GameProgressViewModel extends ViewModel {
    private final GameProgressRepository datasource;

    private final MutableLiveData<GameProgressDto> gameProgressDtoMutableLiveData;

    CompositeDisposable disposable;

    public GameProgressViewModel(GameProgressRepository datasource) {
        this.datasource = datasource;
        this.gameProgressDtoMutableLiveData = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        setupListeners();
    }
    public MutableLiveData<GameProgressDto> getGameProgressDtoMutableLiveData() {
        return gameProgressDtoMutableLiveData;
    }
    private void setupListeners() {
        Disposable boardDisposable = datasource.getGameProgressObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameProgressDtoMutableLiveData::setValue);
        disposable.add(boardDisposable);
    }

    public static final ViewModelInitializer<GameProgressViewModel> initializer = new ViewModelInitializer<>(
            GameProgressViewModel.class,
            creationExtras -> new GameProgressViewModel(GameProgressRepository.getInstance())
    );
}
