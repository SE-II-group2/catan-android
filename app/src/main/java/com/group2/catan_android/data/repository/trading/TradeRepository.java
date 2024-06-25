package com.group2.catan_android.data.repository.trading;


import com.group2.catan_android.data.live.game.TradeOfferDto;
import com.group2.catan_android.data.repository.LiveDataReceiver;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class TradeRepository implements TradeProvider, LiveDataReceiver<TradeOfferDto> {


    PublishSubject<TradeOfferDto> tradeOfferPublishSubject;

    private Flowable<TradeOfferDto> liveDataIn;
    private static TradeRepository instance;
    Disposable d;

    private TradeRepository(){
        this.tradeOfferPublishSubject = PublishSubject.create();

    }

    public static TradeRepository getInstance(){
        if(instance==null)instance=new TradeRepository();
        return instance;
    }
    @Override
    public void setLiveData(Flowable<TradeOfferDto> in) {
        this.liveDataIn=in;
        cleanup();
        wireDataSources();
    }


    private void cleanup() {
        if (d != null)
            d.dispose();
    }

    private void wireDataSources() {
        d = liveDataIn
                .doOnComplete(this::cleanup)
                .subscribe(tradeOfferDto -> tradeOfferPublishSubject.onNext(tradeOfferDto));
    }

    @Override
    public Observable<TradeOfferDto> getTradeObservable() {
        return tradeOfferPublishSubject;
    }

}
