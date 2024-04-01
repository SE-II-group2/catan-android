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

public class ConnectionUnitTest {

    @Test
    public void testRoadOwner() {
        Connection connection = new Road(1);
        assertEquals(1, connection.getPlayerID());
    }
}
