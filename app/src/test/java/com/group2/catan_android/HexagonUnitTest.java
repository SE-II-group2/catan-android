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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.group2.catan_android.fragments.interfaces.ResourceUpdateListener;
import com.group2.catan_android.gamelogic.*;
import com.group2.catan_android.gamelogic.objects.*;
import com.group2.catan_android.gamelogic.enums.*;

import java.util.Arrays;

public class HexagonUnitTest {

    private Player player1;
    private ResourceUpdateListener mockListener;
    @BeforeEach
    public void setUp() {
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
    public void testAddBuilding() {
        Hexagon hexagon = new Hexagon(Location.FOREST,ResourceDistribution.FOREST, 6,false,0);
        Building building1 = new Building(player1, BuildingType.VILLAGE);
        hexagon.addBuilding(building1);

        assertEquals(1, hexagon.getNumOfAdjacentBuildings());
        assertEquals(building1, hexagon.getBuildings()[0]);
    }

    @Mock
    private Building buildingMock;

    @Test
    public void testDistributeResources() {
        buildingMock = mock(Building.class); // Create a mock object for Building}

        Hexagon hexagon = new Hexagon(Location.HILLS, ResourceDistribution.HILLS, 4,false,1);
        hexagon.addBuilding(buildingMock);

        // Assume giveResources method properly modifies resources for Buildings
        hexagon.distributeResources();

        verify(buildingMock, times(1)).giveResources(hexagon.getDistribution());
    }

    @Test
    public void testDistributeResourcesWithKnight(){
        buildingMock = mock(Building.class);
        Hexagon hexagon = new Hexagon(Location.FOREST, ResourceDistribution.FOREST, 5, true, 9);
        hexagon.addBuilding(buildingMock);
        hexagon.distributeResources();
        verify(buildingMock, times(0)).giveResources(hexagon.getDistribution());
    }

    @Test
    public void testHexagonGetter(){
        Hexagon hexagon = new Hexagon(Location.FOREST, ResourceDistribution.FOREST, 5, false, 9);
        Assertions.assertEquals(Location.FOREST, hexagon.getLocation());
        Assertions.assertEquals(5, hexagon.getRollValue());
        Assertions.assertEquals(9, hexagon.getId());
    }

    @Test
    public void testHexagonToString(){
        Hexagon hexagon = new Hexagon(Location.FOREST, ResourceDistribution.FOREST, 5, false, 9);
        String expectedString = "Hexagon Type: FOREST; Rollvalue: 5; Number of Buildings adjecent: 0\n";
        Assertions.assertEquals(expectedString, hexagon.toString());
    }
}
