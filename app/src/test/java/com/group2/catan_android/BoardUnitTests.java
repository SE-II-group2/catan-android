package com.group2.catan_android;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.group2.catan_android.gamelogic.*;
import com.group2.catan_android.gamelogic.objects.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.Before;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardUnitTests {

    enum Location {
        HILLS, FOREST, MOUNTAINS, FIELDS, PASTURE, DESERT
    }

    private Board board;

    @Mock
    private Building buildingMock;

    @BeforeEach
    public void setUp() {
        board = new Board();
        buildingMock = mock(Building.class); // Create a mock object for Building
    }

    @Test
    public void testGenerateHexagonsSize() {
        assertNotNull(board.getHexagonList());
        assertEquals(19, board.getHexagonList().size()); // Check if 19 hexagons are generated
    }

    @Test
    public void testGenerateHexagonsDistribution() {
        List<String> locationsWanted = new ArrayList<>();
        List<Integer> valuesWanted = new ArrayList<>();

        // Copy locations and values lists to ensure original lists remain unchanged
        Collections.addAll(locationsWanted, "HILLS", "HILLS", "HILLS", "FOREST", "FOREST", "FOREST", "FOREST",
                "MOUNTAINS", "MOUNTAINS", "MOUNTAINS", "FIELDS", "FIELDS", "FIELDS", "FIELDS",
                "PASTURE", "PASTURE", "PASTURE", "PASTURE", "DESERT");
        Collections.addAll(valuesWanted, 0, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        List<String> locationsActual = new ArrayList<>();
        List<Integer> valuesActual = new ArrayList<>();

        for (Hexagon hexagon : board.getHexagonList()) {
            locationsActual.add(hexagon.getType());
            valuesActual.add(hexagon.getRollValue());
        }

        Collections.sort(locationsActual);
        Collections.sort(locationsWanted);
        Collections.sort(valuesActual);

        assertEquals(locationsWanted, locationsActual);
        assertEquals(valuesWanted, valuesActual);
    }

    @Test
    public void testGenerateHexagonsDesertTileCorrectness() {
        boolean hasDesertTile = false;
        for (Hexagon hexagon : board.getHexagonList()) {
            if (hexagon.getType().equals(Location.DESERT.name())) {
                assertEquals(0, hexagon.getRollValue());
                assertArrayEquals(new int[]{0, 0, 0, 0, 0}, hexagon.getResourceValue());
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
    public void testAddVillageNormalCase(){
        board.addVillage(1,2,5);
        board.addVillage(1, 2, 7);
        board.addVillage(1, 1, 6);
        List<Hexagon> hexList= board.getHexagonList();
        assertEquals(3, hexList.get(5).getNumOfBuildings());
        assertEquals(1, hexList.get(2).getNumOfBuildings());
        assertEquals(0,hexList.get(3).getNumOfBuildings());

        board.addVillage(1, 2, 6);
        assertEquals(3, hexList.get(5).getNumOfBuildings());
    }

    @Test
    public void testAddVillageEdgeOfBoard(){
        board.addVillage(1, 2, 0);
        board.addVillage(1, 1, 1);

        List<Hexagon> hexList= board.getHexagonList();
        assertEquals(1, hexList.get(7).getNumOfBuildings());
        assertEquals(1,hexList.get(3).getNumOfBuildings());
    }

    @Test
    public void testAddRoad(){
        board.addRoad(1, 0, 1);
        assertTrue(board.isNextToOwnRoad(1, 1));
        assertTrue(board.isNextToOwnRoad(0, 1));
        assertFalse(board.isNextToOwnRoad(8, 1));
    }
}


