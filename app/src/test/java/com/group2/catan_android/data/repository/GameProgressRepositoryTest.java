package com.group2.catan_android.data.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.live.game.IngamePlayerDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.data.repository.gameprogress.GameProgressRepository;
import com.group2.catan_android.gamelogic.Player;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;

public class GameProgressRepositoryTest {
    GameProgressRepository gameProgressRepository;
    PublishProcessor<GameProgressDto> liveIn;
    @BeforeEach
    void setup() {
        gameProgressRepository = GameProgressRepository.getInstance();
        liveIn = PublishProcessor.create();
        gameProgressRepository.setLiveData(liveIn);
    }

    @Test
    void testGameProgressDtoEmitsCorrectValue() {
        TestObserver<GameProgressDto> testObserver = gameProgressRepository.getGameProgressObservable().test();

        GameProgressDto dto = new GameProgressDto(new BuildVillageMoveDto(32));
        liveIn.onNext(dto);
        dto = testObserver.values().get(testObserver.values().size() - 1);

        assert (dto.getGameMoveDto() instanceof BuildVillageMoveDto);
        assertEquals(32, ((BuildVillageMoveDto) dto.getGameMoveDto()).getIntersectionID());
        testObserver.dispose();
    }

    @Test
    void testRollDiceDtoEmitsCorrectValue(){
        TestObserver<RollDiceDto> testObserver = gameProgressRepository.getRollDiceObservable().test();

        GameProgressDto dto = new GameProgressDto(new RollDiceDto(12));
        liveIn.onNext(dto);
        RollDiceDto rollDiceDto = testObserver.values().get(testObserver.values().size() - 1);

        assertEquals(12, rollDiceDto.getDiceRoll());
        testObserver.dispose();
    }

    @Test
    void testEndTurnMoveDtoEmitsCorrectValue(){
        TestObserver<EndTurnMoveDto> testObserver = gameProgressRepository.getEndTurnMoveObservable().test();

        GameProgressDto dto = new GameProgressDto(new EndTurnMoveDto(new IngamePlayerDto("test", new int[]{0,0,0,0,0}, 0,0,0)));
        liveIn.onNext(dto);
        EndTurnMoveDto endTurnMoveDto = testObserver.values().get(testObserver.values().size() - 1);

        assertEquals("test", endTurnMoveDto.getNextPlayer().getDisplayName());
        testObserver.dispose();
    }
}
