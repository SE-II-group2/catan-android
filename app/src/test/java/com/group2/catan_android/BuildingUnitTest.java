package com.group2.catan_android;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import android.graphics.Color;

import com.group2.catan_android.fragments.interfaces.ResourceUpdateListener;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.objects.*;
import com.group2.catan_android.gamelogic.enums.*;

import java.util.Arrays;

public class BuildingUnitTest {

    private Player player1;
    private Player player2;
    private ResourceUpdateListener mockListener;

    @BeforeEach
    public void setUp() {
        player1 = new Player("player1","player1","player1", Color.RED);
        player2 = new Player("player1","player1","player1", Color.RED);
        mockListener = new ResourceUpdateListener() {
            @Override
            public void onResourcesUpdated(int[] resources) {
                System.out.println("Resources updated: " + Arrays.toString(resources));
            }
        };
        player1.setResourceUpdateListener(mockListener);
        player2.setResourceUpdateListener(mockListener);
        player1.adjustResources(new int[]{100,100,100,100,100}); //unlimited resources for testing
        player2.adjustResources(new int[]{100,100,100,100,100}); //unlimited resources for testing
    }

    @Test
    public void testBuildingPlayerID() {
        Building building1 = new Building(player1, BuildingType.VILLAGE);
        Building building2 = new Building(player2, BuildingType.CITY);

        assertEquals(player1, building1.getPlayer());
        assertEquals(player2, building2.getPlayer());
    }

    @Test
    public void testGiveResourcesNormal(){
        Building village = new Building(player1, BuildingType.VILLAGE);
        ResourceDistribution distribution = ResourceDistribution.FIELDS;
        village.giveResources(distribution);
        int[] expectedResources = new int[]{101, 100, 100, 100, 100};
        assertArrayEquals(expectedResources, player1.getResources());
    }

    @Test
    public void testGiveResourcesCity(){
        Building city = new Building(player1, BuildingType.CITY);
        ResourceDistribution distribution = ResourceDistribution.FIELDS;
        city.giveResources(distribution);
        int[] expectedResources = new int[]{102, 100, 100, 100, 100};
        assertArrayEquals(expectedResources, player1.getResources());
    }

    @Test
    public void testBuildingGetter(){
        Building building1 = new Building(player1, BuildingType.VILLAGE);
        Building building2 = new Building(player1, BuildingType.CITY);
        assertEquals(player1, building1.getPlayer());
        assertEquals(BuildingType.VILLAGE, building1.getType());
        assertEquals(BuildingType.CITY, building2.getType());
    }
}
