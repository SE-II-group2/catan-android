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
        Collections.addAll(locationsWanted, "Hills", "Hills", "Hills", "Forest", "Forest", "Forest", "Forest",
                "Mountains", "Mountains", "Mountains", "Fields", "Fields", "Fields", "Fields",
                "Pasture", "Pasture", "Pasture", "Pasture", "Desert");
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
        boolean hasDesertTile=false;
        for (Hexagon hexagon : board.getHexagonList()) {
            if (hexagon.getType().equals("Desert")) {
                assertEquals(0, hexagon.getRollValue());
                assertArrayEquals(new int[]{0,0,0,0,0}, hexagon.getResourceValue());
                hasDesertTile=true;
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
}


