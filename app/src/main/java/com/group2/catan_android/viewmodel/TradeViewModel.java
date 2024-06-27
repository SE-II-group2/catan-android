package com.group2.catan_android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.live.game.TradeOfferDto;
import com.group2.catan_android.data.repository.trading.TradeRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TradeViewModel extends ViewModel {
    private final TradeRepository datasource;

    private final MutableLiveData<TradeOfferDto> tradeOfferDtoMutableLiveData;

    CompositeDisposable disposable;

    public TradeViewModel(TradeRepository datasource) {
        this.datasource = datasource;
        this.tradeOfferDtoMutableLiveData = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        setupListeners();
    }
    public MutableLiveData<TradeOfferDto> getTradeOfferDtoMutableLiveData() {
        return tradeOfferDtoMutableLiveData;
    }
    private void setupListeners() {
        Disposable boardDisposable = datasource.getTradeObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tradeOfferDtoMutableLiveData::setValue);
        disposable.add(boardDisposable);
    }

    public static final ViewModelInitializer<TradeViewModel> initializer = new ViewModelInitializer<>(
            TradeViewModel.class,
            creationExtras -> new TradeViewModel(TradeRepository.getInstance())
    );
}
