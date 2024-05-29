package com.group2.catan_android.data.service;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MoveMakerTest {
    private MoveMaker moveMaker;
    List<Player> playerList;
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

        // Create a spy of MoveMaker with mock dependencies
        moveMaker = spy(new MoveMaker(board, localPlayer, playerList));

        // Mock the sendMove method to do nothing
        doNothing().when(moveMaker).sendMove(any());
        moveMaker.setToken(token);

        isSetupPhaseField = MoveMaker.class.getDeclaredField("isSetupPhase");
        isSetupPhaseField.setAccessible(true);
    }

    @Test
    void testMakeMoveNotActivePlayer() {
        playerList.remove(0);
        GameMoveDto move = new BuildVillageMoveDto(2);
        assertThrows(Exception.class, () -> moveMaker.makeMove(move)); // This should throw an exception
    }


    @Test
    void testMakeRollDiceMoveSuccess() throws Exception {
        GameMoveDto move = new RollDiceDto(5); // Use a proper RollDiceDto instance
        moveMaker.makeMove(move); // This should succeed
        assert (moveMaker.hasRolled());
        verify(moveMaker, times(1)).sendMove(move);
    }

    @Test
    void testMakeRollDiceMoveAlreadyRolled() throws Exception {
        GameMoveDto move = new RollDiceDto(5);
        moveMaker.makeMove(move);
        assertThrows(Exception.class, () -> moveMaker.makeMove(move)); // This should throw an exception
    }

    @Test
    void testMakeEndTurnMoveInSetupPhase() {
        GameMoveDto move = new EndTurnMoveDto();
        assertThrows(Exception.class, () -> moveMaker.makeMove(move));
    }

    @Test
    void testMakeBuildVillageMoveSuccess() throws Exception {
        BuildVillageMoveDto move = new BuildVillageMoveDto(31);
        moveMaker.makeMove(move); // This should succeed
        verify(moveMaker, times(1)).sendMove(move);
    }

    @Test
    void testMakeBuildVillageMoveAlreadyPlacedVillage() throws Exception {
        BuildVillageMoveDto move = new BuildVillageMoveDto();
        moveMaker.makeMove(move);
        assertThrows(Exception.class, () -> moveMaker.makeMove(move));
    }

    @Test
    void testMakeBuildRoadMovePlaceVillageFirst() {
        BuildRoadMoveDto move = new BuildRoadMoveDto();
        assertThrows(Exception.class, () -> moveMaker.makeMove(move));
    }

    @Test
    void testMakeBuildRoadMoveSuccess() throws Exception {
        BuildVillageMoveDto villageMove = new BuildVillageMoveDto(0);
        moveMaker.makeMove(villageMove);

        BuildRoadMoveDto roadMove = new BuildRoadMoveDto(0);
        moveMaker.makeMove(roadMove);
        verify(moveMaker, times(2)).sendMove(any());
    }

    @Test
    void testMakeBuildRoadMoveNotEnoughResources() throws Exception {
        isSetupPhaseField.set(moveMaker, false);
        BuildVillageMoveDto villageMoveDto = new BuildVillageMoveDto();
        assertThrows(Exception.class, () -> moveMaker.makeMove(villageMoveDto));

        BuildRoadMoveDto roadMove = new BuildRoadMoveDto();
        assertThrows(Exception.class, () -> moveMaker.makeMove(roadMove));
    }

    @Test
    void testEndTurnMoveOutOfSetupPhaseSuccess() throws Exception {
        isSetupPhaseField.set(moveMaker, false);
        EndTurnMoveDto endTurnMoveDto = new EndTurnMoveDto();
        moveMaker.makeMove(endTurnMoveDto);

        verify(moveMaker, times(1)).sendMove(any());
    }

    @Test
    void testBuildVillageInvalidLocation() throws Exception {
        isSetupPhaseField.set(moveMaker, false);
        playerList.get(0).adjustResources(new int[]{5, 5, 5, 5, 5});

        BuildVillageMoveDto buildVillageMoveDto = new BuildVillageMoveDto(5);
        moveMaker.makeMove(buildVillageMoveDto);

        assertThrows(Exception.class, () -> moveMaker.makeMove(buildVillageMoveDto));
    }

    @Test
    void testBuildRoadInvalidLocation() throws Exception {
        isSetupPhaseField.set(moveMaker, false);
        playerList.get(0).adjustResources(new int[]{5, 5, 5, 5, 5});

        BuildRoadMoveDto buildRoadMoveDto = new BuildRoadMoveDto(5);

        assertThrows(Exception.class, () -> moveMaker.makeMove(buildRoadMoveDto));
    }

    @Test
    void testIsSetupPhaseReturnsCorrectValue() {
        assertTrue(moveMaker.isSetupPhase());
    }
}

