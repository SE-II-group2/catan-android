package com.group2.catan_android.data.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.repository.gameprogress.GameProgressRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;

public class GameProgressRepositoryTest {
    GameProgressRepository gameProgressRepository;

    @BeforeEach
    void setup() {
        gameProgressRepository = GameProgressRepository.getInstance();
    }

    @Test
    void testGameProgressDtoEmitsCorrectValue() {
        PublishProcessor<GameProgressDto> liveIn = PublishProcessor.create();
        gameProgressRepository.setLiveData(liveIn);
        TestObserver<GameProgressDto> testObserver = gameProgressRepository.getGameProgressObservable().test();

        GameProgressDto dto = new GameProgressDto(new BuildVillageMoveDto(32));
        liveIn.onNext(dto);
        dto = testObserver.values().get(testObserver.values().size() - 1);

        assert (dto.getGameMoveDto() instanceof BuildVillageMoveDto);
        assertEquals(32, ((BuildVillageMoveDto) dto.getGameMoveDto()).getIntersectionID());
        testObserver.dispose();
    }
}
