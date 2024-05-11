package com.group2.catan_android;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.group2.catan_android.fragments.interfaces.ResourceUpdateListener;
import com.group2.catan_android.gamelogic.*;
import com.group2.catan_android.gamelogic.objects.*;
import com.group2.catan_android.gamelogic.enums.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.graphics.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

public class BoardUnitTests {

    private Board board;

    @Mock
    private Building buildingMock;
    private Player player1;
    private ResourceUpdateListener mockListener;

    @BeforeEach
    void setUp() {
        board = new Board();
        buildingMock = mock(Building.class); // Create a mock object for Building
        mockListener = new ResourceUpdateListener() {
            @Override
            public void onResourcesUpdated(int[] resources) {
                System.out.println("Resources updated: " + Arrays.toString(resources));
            }
        };
        player1 = new Player("player1","player1","player1", Color.RED);
        player1.setResourceUpdateListener(mockListener);
        player1.adjustResources(new int[]{100,100,100,100,100}); //unlimited resources for testing
    }

    @Test
    void testGenerateHexagonsSize() {
        assertNotNull(board.getHexagonList());
        assertEquals(19, board.getHexagonList().size()); // Check if 19 hexagons are generated
    }

    @Test
    void testGenerateHexagonsDesertTileCorrectness() {
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
    void testDistributeResourcesByDiceRoll() {
        for (Hexagon hexagon : board.getHexagonList()) {
            hexagon.addBuilding(buildingMock);
        }
        board.distributeResourcesByDiceRoll(6);

        verify(buildingMock, times(2)).giveResources(any());
    }

    @Test
    void testAddVillageNormalCase() {
        board.addNewRoad(player1,14);
        board.addNewRoad(player1,28);
        board.addNewRoad(player1,29);

        board.addNewVillage(player1, 21);
        board.addNewVillage(player1, 23);
        assertFalse(board.addNewVillage(player1, 22));
        board.addNewVillage(player1, 12);

        List<Hexagon> hexList = board.getHexagonList();

        assertEquals(3, hexList.get(5).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(2).getNumOfAdjacentBuildings());
        assertEquals(0, hexList.get(3).getNumOfAdjacentBuildings());
    }

    @Test
    void testAddCityToVillage() {
        board.addNewRoad(player1,27);
        assertSame(BuildingType.EMPTY,board.getIntersections()[2][5].getType());
        board.setSetupPhase(false);

        assertFalse(board.addNewCity(player1, 21));
        assertFalse(board.getIntersections()[2][5] instanceof Building);

        board.addNewVillage(player1, 21);
        assertSame(BuildingType.VILLAGE, board.getIntersections()[2][5].getType());

        board.addNewCity(player1, 21);
        assertSame(BuildingType.CITY, board.getIntersections()[2][5].getType());
    }

    @Test
    void testAddCityNormalCase() {
        board.addNewRoad(player1,14);
        board.addNewRoad(player1,28);
        board.addNewRoad(player1,29);
        board.addNewVillage(player1, 21);
        board.addNewVillage(player1, 23);
        board.addNewVillage(player1, 12);
        board.addNewCity(player1, 21);
        board.addNewCity(player1, 23);
        board.addNewCity(player1, 12);

        List<Hexagon> hexList = board.getHexagonList();

        assertEquals(3, hexList.get(5).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(2).getNumOfAdjacentBuildings());
        assertEquals(0, hexList.get(3).getNumOfAdjacentBuildings());
    }

    @Test
    void testAddCityInsufficientResources(){
        board.setSetupPhase(false);
        player1.adjustResources(new int[]{-100, -100, -100, -100, -100});
        assertFalse(board.addNewCity(player1, 0));
    }
    @Test
    void testAddVillageEdgeOfBoard() {
        board.addNewRoad(player1,18);
        board.addNewRoad(player1,23);

        board.addNewVillage(player1, 16);
        board.addNewVillage(player1, 7);

        List<Hexagon> hexList = board.getHexagonList();
        assertEquals(1, hexList.get(7).getNumOfAdjacentBuildings());
        assertEquals(1, hexList.get(3).getNumOfAdjacentBuildings());
    }

    @Test
    void testAddVillageSetupUpPhase(){
        assertTrue(board.addNewVillage(player1, 16));
        assertTrue(board.addNewRoad(player1,23));

        board.setSetupPhase(false);

        assertFalse(board.addNewVillage(player1,7));
        assertTrue(board.addNewRoad(player1,18));
        assertTrue(board.addNewVillage(player1,7));
    }

    @Test
    void testAddCitySetupUpPhase(){
        assertTrue(board.addNewVillage(player1, 16));
        assertFalse(board.addNewCity(player1,16));

        board.setSetupPhase(false);

        assertTrue(board.addNewCity(player1,16));
    }

    @Test
    void testAddRoad() {
        board.addNewRoad(player1, 0);
        assertTrue(board.isNextToOwnRoad(1, player1));
        assertTrue(board.isNextToOwnRoad(0, player1));
        assertFalse(board.isNextToOwnRoad(8, player1));
    }

    @Test
    void testAddRoadInvalidPlacement(){
        assertTrue(board.addNewRoad(player1, 0));
        assertFalse(board.addNewRoad(player1, 0));

        board.setSetupPhase(false);

        assertTrue(board.addNewRoad(player1,1));
        assertFalse(board.addNewRoad(player1,3));
    }
    @Test
    void testAddRoadInsufficientResources(){
        board.setSetupPhase(false);
        player1.adjustResources(new int[]{-100, -100, -100, -100, -100});
        assertFalse(board.addNewRoad(player1, 0));
    }

    @Test
    void testAddVillageNextToVillage(){
        board.addNewRoad(player1,0);
        board.addNewRoad(player1,6);
        board.addNewRoad(player1,1);
        board.addNewRoad(player1,2);
        board.addNewRoad(player1,7);
        board.addNewRoad(player1,13);

        assertTrue(board.addNewVillage(player1, 0));
        assertFalse(board.addNewVillage(player1, 8));
        assertTrue(board.addNewVillage(player1, 3));
        assertTrue(board.addNewVillage(player1, 11));
    }

    @Test
    void testAddVillageInsufficientResources(){
        board.setSetupPhase(false);
        player1.adjustResources(new int[]{-100, -100, -100, -100, -100});
        assertFalse(board.addNewVillage(player1, 0));
    }

    @Test
    void testGetAdjacencyMatrixCorrectRetrieval() {
        Board board = new Board();
        Connection[][] matrix = board.getAdjacencyMatrix();

        assertNotNull(matrix);
        assertEquals(54, matrix.length);
        assertTrue(matrix[0][1] instanceof Connection || matrix[0][1] == null);
    }

    @Test
    void testTranslateIntersectionToMatrixCoordinates() {
        int[] coords;

        // First Row
        coords = board.translateIntersectionToMatrixCoordinates(1);
        assertArrayEquals(new int[]{0, 3}, coords);

        // Second Row
        coords = board.translateIntersectionToMatrixCoordinates(19);

        assertArrayEquals(new int[]{2, 3}, coords);

        // Third Row
        coords = board.translateIntersectionToMatrixCoordinates(30);

        assertArrayEquals(new int[]{3, 3}, coords);

        // Fourth Row
        coords = board.translateIntersectionToMatrixCoordinates(41);

        assertArrayEquals(new int[]{4, 4}, coords);

        // Fifth Row
        coords = board.translateIntersectionToMatrixCoordinates(50);
        System.out.println("x-Koordinate: " + coords[0]);
        System.out.println("y-Koordinate: " + coords[1]);
        assertArrayEquals(new int[]{5, 5}, coords);
    }

}