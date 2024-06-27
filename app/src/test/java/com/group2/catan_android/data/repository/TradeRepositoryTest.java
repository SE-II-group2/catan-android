package com.group2.catan_android.data.repository;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.live.game.IngamePlayerDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.data.live.game.TradeOfferDto;
import com.group2.catan_android.data.repository.gameprogress.GameProgressRepository;
import com.group2.catan_android.data.repository.trading.TradeRepository;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;

public class TradeRepositoryTest {
    TradeRepository tradeRepository;
    PublishProcessor<TradeOfferDto> liveIn;

    @BeforeEach
    void setup() {
        tradeRepository = TradeRepository.getInstance();
        liveIn = PublishProcessor.create();
        tradeRepository.setLiveData(liveIn);
    }

    @Test
    void testCorrectTradeOfferDtoGetsSend() {
        TestObserver<TradeOfferDto> testObserver = tradeRepository.getTradeObservable().test();

        TradeOfferDto dto = new TradeOfferDto(new int[]{1, 2, 3, 4, 5}, new int[]{5, 4, 3, 2, 1}, new IngamePlayerDto("", new int[]{0, 0, 0, 0, 0}, 3, 7, 3, new ArrayList<>()));
        liveIn.onNext(dto);
        dto = testObserver.values().get(testObserver.values().size() - 1);

        Assertions.assertArrayEquals(new int[]{5, 4, 3, 2, 1}, dto.getGetResources());
        Assertions.assertArrayEquals(new int[]{1, 2, 3, 4, 5}, dto.getGiveResources());
        Assertions.assertEquals(3, dto.getFromPlayer().getInGameID());
        Assertions.assertEquals("", dto.getFromPlayer().getDisplayName());
        testObserver.dispose();

    }
}