package com.group2.catan_android;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import android.graphics.Color;

import com.group2.catan_android.fragments.interfaces.ResourceUpdateListener;
import com.group2.catan_android.gamelogic.Player;

import com.group2.catan_android.gamelogic.enums.ResourceDistribution;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class PlayerUnitTests {

    Player player1;
    ResourceUpdateListener mockListener;
    String playerToken = "player1";
    String displayName = "player1";
    String gameID = "player1";
    static int playerColor = Color.RED;

    @BeforeEach
    public void setUp() {
        player1 = new Player(playerToken,displayName,gameID, playerColor);
        mockListener = new ResourceUpdateListener() {
            @Override
            public void onResourcesUpdated(int[] resources) {
                System.out.println("Resources updated: " + Arrays.toString(resources));
            }
        };
    }

    @Test
    public void testAdjustResources() {
        player1.setResourceUpdateListener(mockListener);
        player1.adjustResources(ResourceDistribution.FOREST.getDistribution());
        assertArrayEquals(ResourceDistribution.FOREST.getDistribution(), player1.getResources());
    }

    @Test
    public void testAdjustResourcesNull(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player1.adjustResources(null);
        });
    }

    @Test
    public void testAdjustResourcesWrongLength(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player1.adjustResources((new int[]{1}));
        });
    }

    @Test
    public void testAdjustResourcesWithoutListener(){
        player1.setResourceUpdateListener(null);
        int[] distribution = new int[]{1, 1, 1, 1, 1};
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player1.adjustResources(distribution);
        });
    }

    @Test
    public void testVictoryPoints() {
        player1.increaseVictoryPoints(2);
        assertEquals(2,player1.getVictoryPoints());

        player1.increaseVictoryPoints(-1);
        assertEquals(1,player1.getVictoryPoints());
    }

    @Test
    public void testResourceSufficient() {
        player1.setResourceUpdateListener(mockListener);
        player1.adjustResources(ResourceDistribution.FOREST.getDistribution());

        int[] costs1 = new int[]{0, 0, -1, 0, 0}; // -FOREST
        int[] costs2 = new int[]{0, -1, -1, 0, 0};
        assertTrue(player1.resourcesSufficient(costs1));
        assertFalse(player1.resourcesSufficient(costs2));
    }

    @Test
    public void testResourceSufficientWithoutCost() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                player1.resourcesSufficient(null);
        });
    }

    @Test
    public void testResourceWrongLength(){
        int[] resourceCost = new int[]{1};
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player1.resourcesSufficient(resourceCost);
        });
    }

    @Test
    public void testPlayerGetter() {
        Assertions.assertEquals(displayName, player1.getDisplayName());
        Assertions.assertEquals(playerColor, player1.getColor());
    }

}
