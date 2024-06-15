package com.group2.catan_android.data.service;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.group2.catan_android.data.live.game.AccuseCheatingDto;
import com.group2.catan_android.data.live.game.BuildCityMoveDto;
import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.live.game.MoveRobberDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.gamelogic.objects.Hexagon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MoveMakerTest {
    private MoveMaker moveMaker;
    private List<Player> playerList;
    private Field isSetupPhaseField;
    private Board board;

    @BeforeEach
    void setup() throws Exception {
        board = new Board();
        List<ProgressCardType> progressCards = new ArrayList<>();
        Player localPlayer = new Player("Local", 0, new int[]{0, 0, 0, 0, 0}, -1, progressCards);
        localPlayer.setInGameID(1);
        Player otherPlayer = new Player("other", 0, new int[]{0, 0, 0, 0, 0}, -2, progressCards);
        otherPlayer.setInGameID(2);
        playerList = new ArrayList<>();
        playerList.add(localPlayer);
        playerList.add(otherPlayer);

        MockitoAnnotations.openMocks(this);

        // Create a spy of MoveMaker with mock dependencies
        moveMaker = spy(new MoveMaker(board, localPlayer, playerList));

        // Mock the sendMove method to do nothing
        doNothing().when(moveMaker).sendMove(any());
        String token = "token";
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
        isSetupPhaseField.set(moveMaker, false);
        GameMoveDto move = new RollDiceDto(5); // Use a proper RollDiceDto instance
        moveMaker.makeMove(move); // This should succeed
        assert (moveMaker.hasRolled());
        verify(moveMaker, times(1)).sendMove(move);
    }

    @Test
    void testMakeRollDiceMoveAlreadyRolled() throws Exception {
        isSetupPhaseField.set(moveMaker, false);
        GameMoveDto move = new RollDiceDto(5);
        moveMaker.makeMove(move);
        assertThrows(Exception.class, () -> moveMaker.makeMove(move)); // This should throw an exception
    }

    @Test
    void testRollDiceDuringSetupPhaseThrowsError(){
        GameMoveDto move = new RollDiceDto(5);
        assertThrows(Exception.class, () -> moveMaker.makeMove(move)); // This should throw an exception
    }

    @Test
    void testMakeEndTurnMoveInSetupPhase() {
        GameMoveDto move = new EndTurnMoveDto();
        assertThrows(Exception.class, () -> moveMaker.makeMove(move));
    }

    @Test
    void testMakeBuildVillageMoveSuccess() {
        BuildVillageMoveDto move = new BuildVillageMoveDto(31);
        moveMaker.makeMove(move); // This should succeed
        verify(moveMaker, times(1)).sendMove(move);
    }


    @Test
    void testMakeBuildVillageMoveAlreadyPlacedVillage() {
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
    void testMakeBuildRoadMoveSuccess() {
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
    void testMoveRobberDuringSetupPhaseThrowsError() {
        MoveRobberDto moveRobberMove = new MoveRobberDto(10, true);
        assertThrows(Exception.class, () -> moveMaker.makeMove(moveRobberMove));
    }

    @Test
    void testMoveRobberThrowsErrorIfNotActivePlayer() throws Exception {
        isSetupPhaseField.set(moveMaker, false);
        playerList.remove(0);
        MoveRobberDto moveRobberMove = new MoveRobberDto(10, true);
        assertThrows(Exception.class, () -> moveMaker.makeMove(moveRobberMove));
    }

    @Test
    void testMoveRobberSendsMovesCorrectly() throws Exception {
        for (Hexagon hexagon : board.getHexagonList()) {
            hexagon.setHasRobber(false);
        }
        isSetupPhaseField.set(moveMaker, false);
        MoveRobberDto moveRobberMove = new MoveRobberDto(10, true);
        moveMaker.makeMove(moveRobberMove);
        moveRobberMove = new MoveRobberDto(15, false);
        moveMaker.makeMove(moveRobberMove);
        verify(moveMaker, times(2)).sendMove(any());
    }

    @Test
    void testMoveRobberToSameFieldThrowsError() throws Exception {
        isSetupPhaseField.set(moveMaker, false);
        //Since every field is a desert field with all robbers when the board is instantiated but hasn't been updated from the server, we can pick any field
        MoveRobberDto moveRobberMove = new MoveRobberDto(10, true);
        assertThrows(Exception.class, () -> moveMaker.makeMove(moveRobberMove));
    }

    @Test
    void testAccuseCheatingDtoSendsCorrectly() throws Exception{
        isSetupPhaseField.set(moveMaker, false);
        AccuseCheatingDto accuseCheatingDto = new AccuseCheatingDto();
        moveMaker.makeMove(accuseCheatingDto);
        verify(moveMaker, times(1)).sendMove(accuseCheatingDto);
    }

    @Test
    void testAccuseCheatingThrowsErrorDuringSetupPhase(){
        AccuseCheatingDto accuseCheatingDto = new AccuseCheatingDto();
        assertThrows(Exception.class, () -> moveMaker.makeMove(accuseCheatingDto));
    }

    @Test
    void testIsSetupPhaseReturnsCorrectValue() {
        Assertions.assertTrue(moveMaker.isSetupPhase());
    }

    @Test
    void testBuildCityInSetupPhase() throws Exception {
        isSetupPhaseField.set(moveMaker, true);
        playerList.get(0).adjustResources(new int[]{5, 5, 5, 5, 5});

        BuildCityMoveDto buildCityMoveDto = new BuildCityMoveDto(5);

        assertThrows(Exception.class, () -> moveMaker.makeMove(buildCityMoveDto));
    }

    @Test
    void testMakeBuildCityMoveSuccess() throws Exception {
        BuildVillageMoveDto villageMove = new BuildVillageMoveDto(0);
        moveMaker.makeMove(villageMove);

        BuildRoadMoveDto roadMove = new BuildRoadMoveDto(0);
        moveMaker.makeMove(roadMove);

        isSetupPhaseField.set(moveMaker, false);
        playerList.get(0).adjustResources(new int[]{5, 5, 5, 5, 5});

        BuildCityMoveDto move = new BuildCityMoveDto(0);
        moveMaker.makeMove(move);
        verify(moveMaker, times(1)).sendMove(move);
    }
}

