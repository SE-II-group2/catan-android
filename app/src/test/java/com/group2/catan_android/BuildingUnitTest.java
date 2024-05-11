package com.group2.catan_android;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import android.graphics.Color;

import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.objects.*;
import com.group2.catan_android.gamelogic.enums.*;

public class BuildingUnitTest {

    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        player1 = new Player("player1","player1","player1", Color.RED);
        player2 = new Player("player1","player1","player1", Color.RED);

        player1.adjustResources(new int[]{100,100,100,100,100}); //unlimited resources for testing
        player2.adjustResources(new int[]{100,100,100,100,100}); //unlimited resources for testing
    }

    @Test
    public void testBuildingPlayerID() {
        Building building1 = new Building(player1, BuildingType.VILLAGE,1);
        Building building2 = new Building(player2, BuildingType.CITY,2);

        assertEquals(player1, building1.getPlayer());
        assertEquals(player2, building2.getPlayer());
    }

    @Test
    void testGiveResourcesNormal(){
        Building village = new Building(player1, BuildingType.VILLAGE,1);
        ResourceDistribution distribution = ResourceDistribution.FIELDS;
        village.giveResources(distribution);
        int[] expectedResources = new int[]{101, 100, 100, 100, 100};
        assertArrayEquals(expectedResources, player1.getResources());
    }

    @Test
    void testGiveResourcesCity(){
        Building city = new Building(player1, BuildingType.CITY,1);
        ResourceDistribution distribution = ResourceDistribution.FIELDS;
        city.giveResources(distribution);
        int[] expectedResources = new int[]{102, 100, 100, 100, 100};
        assertArrayEquals(expectedResources, player1.getResources());
    }

    @Test
    void testBuildingGetter(){
        Building building1 = new Building(player1, BuildingType.VILLAGE,1);
        Building building2 = new Building(player1, BuildingType.CITY,1);
        assertEquals(player1, building1.getPlayer());
        assertEquals(BuildingType.VILLAGE, building1.getType());
        assertEquals(BuildingType.CITY, building2.getType());
    }
}
