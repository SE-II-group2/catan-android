package com.group2.catan_android;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.group2.catan_android.gamelogic.*;
import com.group2.catan_android.gamelogic.objects.*;
import com.group2.catan_android.gamelogic.enums.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.graphics.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

public class BoardUnitTests {

    private Board board;

    @Mock
    private Building buildingMock;
    private Player player1;

    @BeforeEach
    public void setUp() {
        board = new Board();
        buildingMock = mock(Building.class); // Create a mock object for Building
        player1 = new Player("player1", Color.RED);
    }

    @Test
    public void testGenerateHexagonsSize() {
        Assertions.assertNotNull(board.getHexagonList());
        assertEquals(19, board.getHexagonList().size()); // Check if 19 hexagons are generated
    }

    @Test
    public void testGenerateHexagonsDesertTileCorrectness() {
        boolean hasDesertTile = false;
        for (Hexagon hexagon : board.getHexagonList()) {
            if (hexagon.getLocation().equals(Location.DESERT)) {
                assertEquals(0, hexagon.getRollValue());
                assertArrayEquals(new int[]{0, 0, 0, 0, 0}, hexagon.getDistribution().getDistribution());
                hasDesertTile = true;
            }
        }
        assertTrue(hasDesertTile);
    }

    @Test
    public void testDistributeResourcesByDiceRoll() {
        for (Hexagon hexagon : board.getHexagonList()) {
            hexagon.addBuilding(buildingMock);
        }
        board.distributeResourcesByDiceRoll(6);

        verify(buildingMock, times(2)).giveResources(any());
    }

    @Test
    public void testAddVillageNormalCase() {
        board.addNewRoad(player1,14);
        board.addNewRoad(player1,28);
        board.addNewRoad(player1,29);

        board.addNewVillage(player1, 21);
        board.addNewVillage(player1, 23);
        board.addNewVillage(player1, 12);

        List<Hexagon> hexList = board.getHexagonList();

        assertEquals(3, hexList.get(5).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(2).getNumOfAdjacentBuildings());
        assertEquals(0, hexList.get(3).getNumOfAdjacentBuildings());
    }

    @Test
    public void testAddVillageEdgeOfBoard() {
        board.addNewRoad(player1,18);
        board.addNewRoad(player1,23);

        board.addNewVillage(player1, 16);
        board.addNewVillage(player1, 7);

        List<Hexagon> hexList = board.getHexagonList();
        assertEquals(1, hexList.get(7).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(3).getNumOfAdjacentBuildings());
    }

    @Test
    public void testAddVillageSetupUpPhase(){
        assertTrue(board.addNewVillage(player1, 16));
        assertTrue(board.addNewRoad(player1,23));

        board.endSetupPhase();

        assertFalse(board.addNewVillage(player1,7));
        assertTrue(board.addNewRoad(player1,18));
        assertTrue(board.addNewVillage(player1,7));
    }

    @Test
    public void testAddRoad() {
        board.addNewRoad(player1, 0);
        assertTrue(board.isNextToOwnRoad(1, player1));
        assertTrue(board.isNextToOwnRoad(0, player1));
        assertFalse(board.isNextToOwnRoad(8, player1));
    }

    @Test
    public void testAddRoadInvalidPlacement(){
        assertTrue(board.addNewRoad(player1, 0));
        assertFalse(board.addNewRoad(player1, 0));

        board.endSetupPhase();

        assertTrue(board.addNewRoad(player1,1));
        assertFalse(board.addNewRoad(player1,3));
    }
}


