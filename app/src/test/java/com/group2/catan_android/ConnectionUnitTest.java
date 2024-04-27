package com.group2.catan_android;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.group2.catan_android.gamelogic.objects.Connection;
import com.group2.catan_android.gamelogic.objects.Road;

public class ConnectionUnitTest {

    @Test
    public void testRoadOwner() {
        Connection connection = new Road(1);
        assertEquals(1, connection.getPlayerID());
    }
}
