package com.group2.catan_android.data.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catan_android.data.live.PlayerDto;
import com.group2.catan_android.data.live.PlayerState;
import com.group2.catan_android.data.live.PlayersInLobbyDto;
import com.group2.catan_android.data.util.ObjectMapperProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompMessage;

class StompManagerTest {
    @Mock
    StompDriver stompDriver;

    ObjectMapper mapper = new ObjectMapper();
    private StompManager stompManager;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        StompManager.initialize(stompDriver, ObjectMapperProvider.getMapper());
        this.stompManager = StompManager.getInstance();
    }

    @Test
    void testConnectionSuccess(){
        when(stompDriver.connect(any())).thenReturn(Completable.complete());
        when(stompDriver.lifecycle()).thenReturn(PublishProcessor.create());

        TestObserver<Void> observer = stompManager.connect("MyToken").test();
        observer.assertComplete();
    }

    @Test
    void testConnectionFailure(){
        when(stompDriver.connect(any())).thenReturn(Completable.error(new Throwable("Failed to connect")));
        when(stompDriver.lifecycle()).thenReturn(PublishProcessor.create());

        TestObserver<Void> observer = stompManager.connect("MyToken").test();
        observer.assertNotComplete();
    }

    @Test
    void testListenOn(){
        when(stompDriver.connect(any())).thenReturn(Completable.complete());
        when(stompDriver.lifecycle()).thenReturn(PublishProcessor.create());
        PublishProcessor<StompMessage> messagePublisher = PublishProcessor.create();
        when(stompDriver.getTopic(any())).thenReturn(messagePublisher);
        Completable c = stompManager.connect("token").andThen(Completable.fromAction(()-> {
            stompManager.listenPrivate();
        }));
        c.subscribe();
        verify(stompDriver).getTopic(any());
    }

    @Test
    void testFilterByType() throws JsonProcessingException {
        when(stompDriver.connect(any())).thenReturn(Completable.complete());
        when(stompDriver.lifecycle()).thenReturn(PublishProcessor.create());
        PublishProcessor<StompMessage> messagePublisher = PublishProcessor.create();
        when(stompDriver.getTopic(any())).thenReturn(messagePublisher);
        Completable c = stompManager.connect("token").andThen(Completable.fromAction(()-> {
            stompManager.listenPrivate();
        }));
        c.subscribe();

        TestSubscriber<PlayersInLobbyDto> filteredFlowable = stompManager.filterByType(PlayersInLobbyDto.class).test();

        messagePublisher.onNext(new StompMessage(null, null, mapper.writeValueAsString(buildPlayersInLobbyDTO())));

        filteredFlowable.assertValueCount(1);
    }

    @Test
    void testFlowableCompletesIfConnectionTerminates() {
        when(stompDriver.connect(any())).thenReturn(Completable.complete());
        PublishProcessor<LifecycleEvent> lifecycleEventPublisher = PublishProcessor.create();
        when(stompDriver.lifecycle()).thenReturn(lifecycleEventPublisher);
        when(stompDriver.getTopic(any())).thenReturn(PublishProcessor.create());
        Completable c = stompManager.connect("token").andThen(Completable.fromAction(()-> {
            stompManager.listenPrivate();
        }));
        c.subscribe();

        TestSubscriber<PlayersInLobbyDto> filteredFlowable = stompManager.filterByType(PlayersInLobbyDto.class).test();
        filteredFlowable.assertNotComplete();
        lifecycleEventPublisher.onNext(new LifecycleEvent(LifecycleEvent.Type.CLOSED));
        filteredFlowable.assertComplete();
    }

    private PlayersInLobbyDto buildPlayersInLobbyDTO(){
        PlayerDto player1 = new PlayerDto();
        player1.setInGameID(1);
        player1.setDisplayName("player1");
        player1.setState(PlayerState.CONNECTED);

        PlayerDto player2 = new PlayerDto();
        player2.setInGameID(2);
        player2.setDisplayName("player2");
        player2.setState(PlayerState.CONNECTED);

        PlayersInLobbyDto dto = new PlayersInLobbyDto();
        dto.setPlayers(List.of(player1, player2));
        dto.setAdmin(player1);

        return dto;
    }
}
