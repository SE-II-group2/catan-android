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
public class BuildingUnitTest {

    @Test
    public void testBuildingPlayerID() {
        Building building1 = new Building(1, Building.BuildingType.VILLAGE);
        Building building2 = new Building(1, Building.BuildingType.CITY);

        assertEquals(1, building1.getPlayerID());
        assertEquals(1, building2.getPlayerID());
    }
}
