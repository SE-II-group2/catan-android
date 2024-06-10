package com.group2.catan_android.data.repository.trading;

import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.data.live.game.TradeMoveDto;
import com.group2.catan_android.data.live.game.TradeOfferDto;

import io.reactivex.Observable;

public interface TradeProvider {
    Observable<TradeOfferDto> getTradeObservable();

}
