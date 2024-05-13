package com.group2.catan_android;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import android.graphics.Color;

import com.group2.catan_android.gamelogic.Player;

import com.group2.catan_android.gamelogic.enums.ResourceDistribution;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerUnitTests {

    private Player player1;
    private final String playerToken = "player1";
    private final String displayName = "player1";
    private final String gameID = "player1";
    static int playerColor = Color.RED;

    @BeforeEach
    void setUp() {
        player1 = new Player(playerToken,displayName,gameID, playerColor);
    }

    @Test
    void testAdjustResourcesNull(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player1.adjustResources(null);
        });
    }

    @Test
    void testAdjustResourcesWrongLength(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player1.adjustResources((new int[]{1}));
        });
    }

    @Test
    void testVictoryPoints() {
        player1.increaseVictoryPoints(2);
        assertEquals(2,player1.getVictoryPoints());

        player1.increaseVictoryPoints(-1);
        assertEquals(1,player1.getVictoryPoints());
    }

    @Test
    void testResourceSufficient() {
        player1.adjustResources(ResourceDistribution.FOREST.getDistribution());

        int[] costs1 = new int[]{0, 0, -1, 0, 0}; // -FOREST
        int[] costs2 = new int[]{0, -1, -1, 0, 0};
        assertTrue(player1.resourcesSufficient(costs1));
        assertFalse(player1.resourcesSufficient(costs2));
    }

    @Test
    void testResourceSufficientWithoutCost() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                player1.resourcesSufficient(null);
        });
    }

    @Test
    void testResourceWrongLength(){
        int[] resourceCost = new int[]{1};
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            player1.resourcesSufficient(resourceCost);
        });
    }

    @Test
    void testPlayerGetter() {
        Assertions.assertEquals(displayName, player1.getDisplayName());
        Assertions.assertEquals(playerColor, player1.getColor());
    }

}
