package com.group2.catan_android.data.service;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.data.repository.moves.MoveSenderRepository;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.Player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

public class MoveMakerTest {
    private MoveMaker moveMaker;
    List<Player> playerList;
    @Mock
    private MoveSenderRepository mockMoveSenderRepository;
    private final String token = "token";
    Field isSetupPhaseField;

    @BeforeEach
    void setup() throws Exception {
        Board board = new Board();
        Player localPlayer = new Player("Local", 0, new int[]{0, 0, 0, 0, 0}, -1);
        localPlayer.setInGameID(1);
        Player otherPlayer = new Player("other", 0, new int[]{0, 0, 0, 0, 0}, -2);
        otherPlayer.setInGameID(2);
        playerList = new ArrayList<>();
        playerList.add(localPlayer);
        playerList.add(otherPlayer);

        MockitoAnnotations.openMocks(this);

        Constructor<MoveMaker> constructor = MoveMaker.class.getDeclaredConstructor(Board.class, Player.class, List.class);
        constructor.setAccessible(true);
        moveMaker = spy(constructor.newInstance(board, localPlayer, playerList));
        moveMaker.setToken(token);


        // Configure RxJava to use a different scheduler for AndroidSchedulers.mainThread()
        //RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        // Use reflection to set the MoveSenderRepository instance to the mocked one
        try {
            Field moveSenderRepositoryField = MoveMaker.class.getDeclaredField("moveSenderRepository");
            moveSenderRepositoryField.setAccessible(true);
            moveSenderRepositoryField.set(moveMaker, mockMoveSenderRepository);

            Field boardField = MoveMaker.class.getDeclaredField("board");
            boardField.setAccessible(true);
            boardField.set(moveMaker, board);

            Field localPlayerField = MoveMaker.class.getDeclaredField("localPlayer");
            localPlayerField.setAccessible(true);
            localPlayerField.set(moveMaker, localPlayer);

            Field playerListField = MoveMaker.class.getDeclaredField("players");
            playerListField.setAccessible(true);
            playerListField.set(moveMaker, playerList);

            isSetupPhaseField = MoveMaker.class.getDeclaredField("isSetupPhase");
            isSetupPhaseField.setAccessible(true);

            Method method = MoveMaker.class.getDeclaredMethod("sendMove", GameMoveDto.class);
            method.setAccessible(true); // This allows us to access private methods


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        // Reset RxAndroidPlugins to its default state
        //RxAndroidPlugins.reset();
    }

    @Test
    void testMakeMoveNotActivePlayer() {
        playerList.remove(0);
        GameMoveDto move = new BuildVillageMoveDto(2);
        assertThrows(Exception.class, () -> moveMaker.makeMove(move)); // This should throw an exception
    }
/*
    @Test
    void testMakeRollDiceMoveSuccess() throws Exception {
        GameMoveDto move = new RollDiceDto(5); // Use a proper RollDiceDto instance
        moveMaker.makeMove(move); // This should succeed
        assert(moveMaker.hasRolled());
        verify(mockMoveSenderRepository, times(1)).sendMove(move, token);
    }

    @Test
    void testMakeRollDiceMoveAlreadyRolled() throws Exception {
        GameMoveDto move = new RollDiceDto(5); // Use a proper RollDiceDto instance
        moveMaker.makeMove(move);
        assertThrows(Exception.class, () -> moveMaker.makeMove(move)); // This should throw an exception
    }
*/
    @Test
    void testMakeEndTurnMoveInSetupPhase() {
        GameMoveDto move = new EndTurnMoveDto(); // Use a proper EndTurnMoveDto instance
        assertThrows(Exception.class, () -> moveMaker.makeMove(move));
    }

    /*@Test
    void testMakeBuildVillageMoveSuccess() throws Exception {
        BuildVillageMoveDto move = new BuildVillageMoveDto(31);
        moveMaker.makeMove(move); // This should succeed
        verify(mockMoveSenderRepository, times(1)).sendMove(move, token);
    }*/
/*
    @Test
    void testMakeBuildVillageMoveAlreadyPlacedVillage() throws Exception {
        BuildVillageMoveDto move = new BuildVillageMoveDto();
        moveMaker.makeMove(move);
        assertThrows(Exception.class, () -> moveMaker.makeMove(move));
    }
*/
    @Test
    void testMakeBuildRoadMovePlaceVillageFirst() {
        BuildRoadMoveDto move = new BuildRoadMoveDto();
        assertThrows(Exception.class, () -> moveMaker.makeMove(move));
    }

   /* @Test
    void testMakeBuildRoadMoveSuccess() throws Exception {
        BuildVillageMoveDto villageMove = new BuildVillageMoveDto(0);
        moveMaker.makeMove(villageMove);

        BuildRoadMoveDto roadMove = new BuildRoadMoveDto(0);
        moveMaker.makeMove(roadMove);
        verify(mockMoveSenderRepository, times(2)).sendMove(any(), eq(token));
    }*/

    @Test
    void testMakeBuildRoadMoveNotEnoughResources() throws Exception {
        isSetupPhaseField.set(moveMaker, false);
        BuildVillageMoveDto villageMoveDto = new BuildVillageMoveDto();
        assertThrows(Exception.class, () -> moveMaker.makeMove(villageMoveDto));

        BuildRoadMoveDto roadMove = new BuildRoadMoveDto();
        assertThrows(Exception.class, () -> moveMaker.makeMove(roadMove));
    }
}

