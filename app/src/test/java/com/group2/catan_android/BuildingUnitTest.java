package com.group2.catan_android;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.group2.catan_android.gamelogic.objects.Building;

public class BuildingUnitTest {

    @Test
    public void testBuildingPlayerID() {
        Building building1 = new Building(1, Building.BuildingType.VILLAGE);
        Building building2 = new Building(1, Building.BuildingType.CITY);

        Assertions.assertEquals(1, building1.getPlayerID());
        Assertions.assertEquals(1, building2.getPlayerID());
    }
}
