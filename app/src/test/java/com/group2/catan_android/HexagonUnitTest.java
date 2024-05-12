package com.group2.catan_android;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.graphics.Color;

import com.group2.catan_android.gamelogic.*;
import com.group2.catan_android.gamelogic.objects.*;
import com.group2.catan_android.gamelogic.enums.*;

public class HexagonUnitTest {

    private Player player1;

    @BeforeEach
    void setUp() {
        player1 = new Player("player1","player1","player1", Color.RED);
        player1.adjustResources(new int[]{100,100,100,100,100}); //unlimited resources for testing
    }

    @Test
    public void testAddBuilding() {
        Hexagon hexagon = new Hexagon(Hexagontype.FOREST,ResourceDistribution.FOREST, 6,0, false);
        Building building1 = new Building(player1, BuildingType.VILLAGE, 1);
        hexagon.addBuilding(building1);

        assertEquals(1, hexagon.getNumOfAdjacentBuildings());
        assertEquals(building1, hexagon.getBuildings()[0]);
    }

    @Mock
    private Building buildingMock;

    @Test
    void testDistributeResources() {
        buildingMock = mock(Building.class); // Create a mock object for Building}

        Hexagon hexagon = new Hexagon(Hexagontype.HILLS, ResourceDistribution.HILLS, 4,1, false);
        hexagon.addBuilding(buildingMock);

        // Assume giveResources method properly modifies resources for Buildings
        hexagon.distributeResources();

        verify(buildingMock, times(1)).giveResources(hexagon.getDistribution());
    }

    @Test
    void testDistributeResourcesWithKnight(){
        buildingMock = mock(Building.class);
        Hexagon hexagon = new Hexagon(Location.FOREST, ResourceDistribution.FOREST, 5, true, 9);
        hexagon.addBuilding(buildingMock);
        hexagon.distributeResources();
        verify(buildingMock, times(0)).giveResources(hexagon.getDistribution());
    }

    @Test
    void testHexagonGetter(){
        Hexagon hexagon = new Hexagon(Location.FOREST, ResourceDistribution.FOREST, 5, false, 9);
        Assertions.assertEquals(Location.FOREST, hexagon.getLocation());
        Assertions.assertEquals(5, hexagon.getRollValue());
        Assertions.assertEquals(9, hexagon.getId());
    }

    @Test
    void testHexagonToString(){
        Hexagon hexagon = new Hexagon(Location.FOREST, ResourceDistribution.FOREST, 5, false, 9);
        String expectedString = "Hexagon Type: FOREST; Rollvalue: 5; Number of Buildings adjecent: 0\n";
        Assertions.assertEquals(expectedString, hexagon.toString());
    }
}
