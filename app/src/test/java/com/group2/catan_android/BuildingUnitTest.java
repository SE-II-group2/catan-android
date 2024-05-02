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
    public void setUp() {
        player1 = new Player("player1","player1","player1", Color.RED);
        player2 = new Player("player1","player1","player1", Color.RED);
    }

    @Test
    public void testBuildingPlayerID() {
        Building building1 = new Building(player1, BuildingType.VILLAGE);
        Building building2 = new Building(player2, BuildingType.CITY);

        assertEquals(player1, building1.getPlayer());
        assertEquals(player2, building2.getPlayer());
    }
}
