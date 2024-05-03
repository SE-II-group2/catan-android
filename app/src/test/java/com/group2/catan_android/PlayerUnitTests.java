package com.group2.catan_android;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import android.graphics.Color;

import com.group2.catan_android.gamelogic.Player;

import com.group2.catan_android.gamelogic.enums.ResourceDistribution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerUnitTests {

    Player player1;

    @BeforeEach
    public void setUp() {
        player1 = new Player("player1","player1","player1", Color.RED);
    }

    @Test
    public void testAdjustResources() {
        player1.adjustResources(ResourceDistribution.FOREST.getDistribution());

        assertArrayEquals(ResourceDistribution.FOREST.getDistribution(), player1.getResources());
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
        player1.adjustResources(ResourceDistribution.FOREST.getDistribution());

        int[] costs1 = new int[]{0, 0, -1, 0, 0}; // -FOREST
        int[] costs2 = new int[]{0, -1, -1, 0, 0};
        assertTrue(player1.resourcesSufficient(costs1));
        assertFalse(player1.resourcesSufficient(costs2));
    }
}
