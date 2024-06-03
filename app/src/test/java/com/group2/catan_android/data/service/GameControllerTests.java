package com.group2.catan_android.data.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.group2.catan_android.data.api.JoinGameRequest;
import com.group2.catan_android.data.api.JoinGameResponse;
import com.group2.catan_android.data.live.PlayersInLobbyDto;
import com.group2.catan_android.data.repository.gameprogress.GameProgressRepository;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.data.repository.lobby.LobbyJoiner;
import com.group2.catan_android.data.repository.player.PlayerRepository;
import com.group2.catan_android.data.repository.token.TokenRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

class GameControllerTests {
    GameController gameController;
    @Mock
    StompManager stompManager;
    @Mock
    TokenRepository tokenRepository;
    @Mock
    PlayerRepository playerRepository;
    @Mock
    LobbyJoiner lobbyJoiner;

    @Mock
    CurrentGamestateRepository currentGamestateRepository;

    @Mock
    GameProgressRepository gameProgressRepository;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        GameController.initialize(stompManager, tokenRepository, playerRepository, lobbyJoiner, currentGamestateRepository, gameProgressRepository);
        gameController = GameController.getInstance();
    }

    @Test
    void testJoinExistingGameSuccess(){
        JoinGameRequest request = new JoinGameRequest();
        request.setGameID("gameID");
        request.setPlayerName("Player");

        JoinGameResponse response = new JoinGameResponse();
        response.setToken("myToken");
        response.setInGameID(1);
        response.setGameID(request.getGameID());
        response.setPlayerName(request.getPlayerName());

        when(lobbyJoiner.joinGame(any())).thenReturn(Single.just(response));
        when(stompManager.connect(response.getToken())).thenReturn(Completable.complete());
        when(stompManager.filterByType(PlayersInLobbyDto.class)).thenReturn(PublishProcessor.create());

        Disposable d = gameController.joinGame(request).subscribe(() -> {
            verify(lobbyJoiner).joinGame(any());
            verify(stompManager).connect(response.getToken());
            verify(stompManager, times(2)).listenOnGame(any());
            verify(stompManager).filterByType(PlayersInLobbyDto.class);

            verify(playerRepository).setLiveData(any());
            verify(playerRepository).setCurrentPlayerID(response.getInGameID());

            verify(tokenRepository).storeToken(response.getToken());
            verify(tokenRepository).storeInGameID(response.getInGameID());
            verify(tokenRepository).storeGameID(response.getGameID());
            },
                t -> Assertions.fail()
        );
        d.dispose();
    }

    @Test
    void testJoinExistingGameFailsOnInitialRequest(){
        JoinGameRequest request = new JoinGameRequest();
        request.setGameID("gameID");
        request.setPlayerName("Player");

        when(lobbyJoiner.joinGame(any())).thenReturn(Single.error(new Throwable()));
       gameController.joinGame(request).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Assertions.fail("Should not complete");
            }

            @Override
            public void onError(Throwable e) {
                verify(lobbyJoiner).joinGame(any());
                verify(stompManager, never()).connect(any());
                verify(playerRepository, never()).setLiveData(any());
            }
        });
    }

    @Test
    void testJoinExistingGameFailsOnSocketConnection(){
        JoinGameRequest request = new JoinGameRequest();
        request.setGameID("gameID");
        request.setPlayerName("Player");

        JoinGameResponse response = new JoinGameResponse();
        response.setToken("myToken");
        response.setInGameID(1);
        response.setGameID(request.getGameID());
        response.setPlayerName(request.getPlayerName());

        when(lobbyJoiner.joinGame(any())).thenReturn(Single.just(response));
        when(stompManager.connect(any())).thenReturn(Completable.error(new Throwable()));
        gameController.joinGame(request).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Assertions.fail("Should not complete");
            }

            @Override
            public void onError(Throwable e) {
                verify(lobbyJoiner).joinGame(any());
                verify(stompManager).connect(response.getToken());
                verify(playerRepository, never()).setLiveData(any());
            }
        });
    }

    @Test
    void testCanCreateNewGame(){
        JoinGameRequest request = new JoinGameRequest();
        request.setGameID("gameID");
        request.setPlayerName("Player");

        JoinGameResponse response = new JoinGameResponse();
        response.setToken("myToken");
        response.setInGameID(1);
        response.setGameID(request.getGameID());
        response.setPlayerName(request.getPlayerName());

        when(lobbyJoiner.createGame(any())).thenReturn(Single.just(response));
        when(stompManager.connect(response.getToken())).thenReturn(Completable.complete());
        when(stompManager.filterByType(PlayersInLobbyDto.class)).thenReturn(PublishProcessor.create());

        Disposable d = gameController.joinGame(request).subscribe(() -> {
                    verify(lobbyJoiner).joinGame(any());
                    verify(stompManager).connect(response.getToken());
                    verify(stompManager, times(2)).listenOnGame(any());
                    verify(stompManager).filterByType(PlayersInLobbyDto.class);

                    verify(playerRepository).setLiveData(any());
                    verify(playerRepository).setCurrentPlayerID(response.getInGameID());

                    verify(tokenRepository).storeToken(response.getToken());
                    verify(tokenRepository).storeInGameID(response.getInGameID());
                    verify(tokenRepository).storeGameID(response.getGameID());

                    verify(gameProgressRepository).setLiveData(any());

                    verify(currentGamestateRepository).setLiveData(any());
                },
                t -> Assertions.fail()
        );
        d.dispose();
    }

    @Test
    void testLeaveGameSuccess(){
        when(tokenRepository.getToken()).thenReturn("MyToken");
        when(lobbyJoiner.leaveGame(any())).thenReturn(Completable.complete());

        gameController.leaveGame().subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                verify(stompManager).shutdown();
            }

            @Override
            public void onError(Throwable e) {
                Assertions.fail("Should not fail");
            }
        });

    }
}
