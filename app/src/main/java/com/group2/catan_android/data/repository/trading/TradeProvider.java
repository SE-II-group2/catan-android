package com.group2.catan_android.data.repository.trading;

import com.group2.catan_android.data.live.game.TradeOfferDto;

import io.reactivex.Observable;

public interface TradeProvider {
    Observable<TradeOfferDto> getTradeObservable();

}
