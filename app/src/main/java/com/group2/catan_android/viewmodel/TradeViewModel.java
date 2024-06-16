package com.group2.catan_android.viewmodel;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.live.game.TradeMoveDto;
import com.group2.catan_android.data.live.game.TradeOfferDto;
import com.group2.catan_android.data.repository.gameprogress.GameProgressRepository;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.data.repository.trading.TradeRepository;
import com.group2.catan_android.gamelogic.Board;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TradeViewModel extends ViewModel {
    private final TradeRepository datasource;

    private final MutableLiveData<TradeOfferDto> TradeOfferDtoMutableLiveData;

    CompositeDisposable disposable;

    public TradeViewModel(TradeRepository datasource) {
        this.datasource = datasource;
        this.TradeOfferDtoMutableLiveData = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        setupListeners();
    }
    public MutableLiveData<TradeOfferDto> getTradeOfferDtoMutableLiveData() {
        return TradeOfferDtoMutableLiveData;
    }
    private void setupListeners() {
        //Toast.makeText(getApplicationContext(), "Why does this apply when create game?", Toast.LENGTH_LONG).show();
        Disposable boardDisposable = datasource.getTradeObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(TradeOfferDtoMutableLiveData::setValue);
        disposable.add(boardDisposable);
    }

    public static final ViewModelInitializer<TradeViewModel> initializer = new ViewModelInitializer<>(
            TradeViewModel.class,
            creationExtras -> new TradeViewModel(TradeRepository.getInstance())
    );
}
