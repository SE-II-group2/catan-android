package com.group2.catan_android.data.repository;

import com.group2.catan_android.data.live.PlayerDto;
import com.group2.catan_android.data.live.PlayersInLobbyDto;
import com.group2.catan_android.data.model.DisplayablePlayer;
import com.group2.catan_android.data.repository.player.PlayerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;

public class PlayerRepositoryTest {
    private PlayerRepository playerRepository;
    @BeforeEach
    public void setUp() {
        playerRepository = PlayerRepository.getInstance();
    }

    @Test
    void testAdminObservableEmitsTrueIfPlayerIsAdmin(){
        PublishProcessor<PlayersInLobbyDto> liveIn = PublishProcessor.create();
        playerRepository.setLiveData(liveIn);
        playerRepository.setCurrentPlayerID(1);
        TestObserver<Boolean> testObserver = playerRepository.getAdminObservable().test();

        //Simulates the repository for player with id 1. Player 0 is currently admin.

        PlayerDto player0 = new PlayerDto();
        PlayerDto player1 = new PlayerDto();
        player0.setInGameID(0);
        player1.setInGameID(1);

        PlayersInLobbyDto dto = new PlayersInLobbyDto();
        dto.setAdmin(player0);
        dto.setPlayers(List.of(player0, player1));

        liveIn.onNext(dto);
        assertLastValueEquals(testObserver.values(), false);

        //now player0 leaves the game
        dto.setPlayers(List.of(player1));
        dto.setAdmin(player1);
        liveIn.onNext(dto);
        assertLastValueEquals(testObserver.values(), true);
        testObserver.dispose();
    }

    @Test
    void testPlayerObservableEmitsExpectedValues(){
        PublishProcessor<PlayersInLobbyDto> liveIn = PublishProcessor.create();
        playerRepository.setLiveData(liveIn);

        PlayerDto player1 = new PlayerDto();
        player1.setInGameID(1);
        PlayerDto player2 = new PlayerDto();
        player2.setInGameID(2);

        PlayersInLobbyDto playersInLobbyDto = new PlayersInLobbyDto();
        playersInLobbyDto.setAdmin(player1);
        playersInLobbyDto.setPlayers(List.of(player2, player1));

        TestObserver<List<DisplayablePlayer>> testObserver = playerRepository.getPlayerObservable().test();

        liveIn.onNext(playersInLobbyDto);
        testObserver.assertValueAt(0, List::isEmpty);
        List<DisplayablePlayer> list = testObserver.values().get(1);

        assertEquals(2, list.size());
        for(DisplayablePlayer p : list){
            if(p.getInGameID() == 1){
                assertTrue(p.isAdmin());
            }
            if(p.getInGameID() == 2){
                assertFalse(p.isAdmin());
            }
        }
    }

    @Test
    void testCleanupResetsValuesToDefault(){
        PublishProcessor<PlayersInLobbyDto> liveIn = PublishProcessor.create();
        playerRepository.setLiveData(liveIn);
        playerRepository.setCurrentPlayerID(0);

        PlayerDto player1 = new PlayerDto();
        player1.setInGameID(0);
        PlayersInLobbyDto dto = new PlayersInLobbyDto();
        dto.setPlayers(List.of(player1));
        dto.setAdmin(player1);

        liveIn.onNext(dto);

        TestObserver<Boolean> adminObserver = playerRepository.getAdminObservable().test();
        TestObserver<List<DisplayablePlayer>> playerObserver = playerRepository.getPlayerObservable().test();

        playerRepository.setLiveData(PublishProcessor.create());

        assertLastValueEquals(adminObserver.values(), false);
        assertLastValueEquals(playerObserver.values(), List.of());

        adminObserver.assertNotComplete();
        playerObserver.assertNotComplete();
    }

    <T> void assertLastValueEquals(List<T> values, T expected){
        int lastIndex = values.size();
        assertEquals(values.get(lastIndex - 1), expected);
    }
}
