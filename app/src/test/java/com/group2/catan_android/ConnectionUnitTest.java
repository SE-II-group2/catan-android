package com.group2.catan_android;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import android.graphics.Color;

import com.group2.catan_android.gamelogic.*;
import com.group2.catan_android.gamelogic.objects.*;

public class ConnectionUnitTest {

    private Player player1;
    @BeforeEach
    public void setUp() {
        player1 = new Player("player1","player1","player1", Color.RED);
    }

    @Test
    public void testRoadOwner() {
        Connection connection = new Road(player1);
        assertEquals(player1, connection.getPlayer());
    }
}
