package com.group2.catan_android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.data.repository.player.PlayerRepository;
import com.group2.catan_android.data.repository.token.TokenRepository;
import com.group2.catan_android.data.service.GameController;
import com.group2.catan_android.gamelogic.Board;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BoardViewModel extends ViewModel {
    private final CurrentGamestateRepository datasource;

    private final MutableLiveData<Board> boardMutableLiveData;

    CompositeDisposable disposable;

    public BoardViewModel(CurrentGamestateRepository datasource) {
        this.datasource = datasource;
        this.boardMutableLiveData = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        setupListeners();
    }
    public MutableLiveData<Board> getBoardMutableLiveData() {
        return boardMutableLiveData;
    }
    private void setupListeners() {
        Disposable boardDisposable = datasource.getCurrentGameStateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentGameState -> {
                    boardMutableLiveData.setValue(currentGameState.getBoard());
                });
        disposable.add(boardDisposable);
    }

    public static final ViewModelInitializer<BoardViewModel> initializer = new ViewModelInitializer<>(
            BoardViewModel.class,
            creationExtras -> new BoardViewModel(CurrentGamestateRepository.getInstance())
    );
}
