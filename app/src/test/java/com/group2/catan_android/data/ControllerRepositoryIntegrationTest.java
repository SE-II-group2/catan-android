package com.group2.catan_android.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catan_android.data.api.JoinGameRequest;
import com.group2.catan_android.data.api.JoinGameResponse;
import com.group2.catan_android.data.live.PlayerDto;
import com.group2.catan_android.data.live.PlayerState;
import com.group2.catan_android.data.live.PlayersInLobbyDto;
import com.group2.catan_android.data.model.DisplayablePlayer;
import com.group2.catan_android.data.repository.lobby.LobbyJoiner;
import com.group2.catan_android.data.repository.player.PlayerRepository;
import com.group2.catan_android.data.repository.token.PreferenceManager;
import com.group2.catan_android.data.repository.token.TokenRepository;
import com.group2.catan_android.data.service.GameController;

import com.group2.catan_android.data.service.StompDriver;
import com.group2.catan_android.data.service.StompManager;
import com.group2.catan_android.data.util.ObjectMapperProvider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompMessage;

public class ControllerRepositoryIntegrationTest {
    @Mock
    StompDriver stompDriver;
    @Mock
    PreferenceManager preferenceManager;
    @Mock
    LobbyJoiner lobbyJoiner;

    ObjectMapper mapper = new ObjectMapper();

    private GameController gameController;
    private PlayerRepository playerRepository;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        this.playerRepository = PlayerRepository.getInstance();
        StompManager.initialize(stompDriver, mapper);
        TokenRepository.initialize(preferenceManager);
        GameController.initialize(StompManager.getInstance(), TokenRepository.getInstance(), playerRepository, lobbyJoiner);
        gameController = GameController.getInstance();
    }

    @Test
    public void testRepositoryReceivesMessagesAfterControllerJoinsGame() throws JsonProcessingException, InterruptedException {
        JoinGameRequest request = new JoinGameRequest();
        request.setPlayerName("Player");
        request.setGameID("MyGameID");

        JoinGameResponse response = new JoinGameResponse();
        response.setPlayerName("Player");
        response.setToken("token");
        response.setGameID("MyGameID");
        response.setInGameID(0);

        PublishProcessor<StompMessage> mockedServerMessages = PublishProcessor.create();
        PublishProcessor<LifecycleEvent> mockedLifecycleEvents = PublishProcessor.create();

        when(lobbyJoiner.joinGame(request)).thenReturn(Single.just(response));
        when(stompDriver.connect("token")).thenReturn(Completable.complete());
        when(stompDriver.getTopic("/topic/game/MyGameID/messages")).thenReturn(mockedServerMessages);
        when(stompDriver.getTopic("/user/queue/messages")).thenReturn(Flowable.never());
        when(stompDriver.lifecycle()).thenReturn(mockedLifecycleEvents);

        gameController.joinGame(request).subscribe();

        TestObserver<Boolean> adminObserver = playerRepository.getAdminObservable().test();
        TestObserver<List<DisplayablePlayer>> playerObserver = playerRepository.getPlayerObservable().test();

        adminObserver.assertNotComplete();
        playerObserver.assertNotComplete();
        adminObserver.assertValue(false);
        playerObserver.assertValue(List::isEmpty);


        //now simulate a incoming PlayersInLobbyDTO;
        PlayersInLobbyDto dto = new PlayersInLobbyDto();
        PlayerDto playerME = new PlayerDto("Player", 0, PlayerState.CONNECTED);
        PlayerDto otherPlayer = new PlayerDto("Other", 1, PlayerState.CONNECTED);
        dto.setAdmin(playerME);
        dto.setPlayers(List.of(playerME, otherPlayer));

        mockedServerMessages.onNext(new StompMessage(null, null, mapper.writeValueAsString(dto)));
        Boolean lastAdminValue = getLastValue(adminObserver);
        assertTrue(lastAdminValue);

        List<DisplayablePlayer> lastPlayerValue = getLastValue(playerObserver);
        assertEquals(2, lastPlayerValue.size());

        //on disconnect values should be null but not completed
        mockedLifecycleEvents.onNext(new LifecycleEvent(LifecycleEvent.Type.CLOSED));

        lastAdminValue = getLastValue(adminObserver);
        assertFalse(lastAdminValue);
        lastPlayerValue = getLastValue(playerObserver);
        assertTrue(lastPlayerValue.isEmpty());

        adminObserver.assertNotComplete();
        playerObserver.assertNotComplete();
    }

    private <T> T getLastValue(TestObserver<T> observer){
        int size = observer.values().size();
        return observer.values().get(size-1);
    }

}
