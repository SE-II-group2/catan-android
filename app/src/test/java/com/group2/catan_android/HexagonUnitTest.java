package com.group2.catan_android;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.group2.catan_android.gamelogic.*;
import com.group2.catan_android.gamelogic.objects.*;

public class HexagonUnitTest {
    @Test
    public void testAddBuilding() {
        Hexagon hexagon = new Hexagon("Forest", new int[]{0, 0, 1, 0, 0}, 6);
        Building building1 = new Village(1);
        hexagon.addBuilding(building1);

        assertEquals(1, hexagon.getNumOfBuildings());
        assertEquals(building1, hexagon.getBuildings()[0]);
    }

    @Mock
    private Building buildingMock;

    @Test
    public void testDistributeResources() {
        buildingMock = mock(Building.class); // Create a mock object for Building}

        Hexagon hexagon = new Hexagon("Hills", new int[]{1, 0, 0, 0, 0}, 4);
        hexagon.addBuilding(buildingMock);

        // Assume giveResources method properly modifies resources for Buildings
        hexagon.distributeResources();

        verify(buildingMock, times(1)).giveResources(hexagon.getResourceValue());
    }
}
