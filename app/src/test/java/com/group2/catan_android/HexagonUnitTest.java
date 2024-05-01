package com.group2.catan_android;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.group2.catan_android.gamelogic.*;
import com.group2.catan_android.gamelogic.objects.*;
import com.group2.catan_android.gamelogic.enums.*;

public class HexagonUnitTest {

    private Player player1;
    @BeforeEach
    public void setUp() {
        player1 = new Player("player1", "color");
    }

    @Test
    public void testAddBuilding() {
        Hexagon hexagon = new Hexagon(Location.FOREST,ResourceDistribution.FOREST, 6,0);
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

        Hexagon hexagon = new Hexagon(Location.HILLS, ResourceDistribution.HILLS, 4,1);
        hexagon.addBuilding(buildingMock);

        // Assume giveResources method properly modifies resources for Buildings
        hexagon.distributeResources();

        verify(buildingMock, times(1)).giveResources(hexagon.getDistribution());
    }
}
