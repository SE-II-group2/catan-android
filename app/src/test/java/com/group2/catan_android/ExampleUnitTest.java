package com.group2.catan_android;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.group2.catan_android.networking.WebSocketClient;

import org.junit.jupiter.api.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
class ExampleUnitTest {
    @Test
    void testConcatenateStringsMethod() {
        String first = "Hello";
        String second = "World";

        String result = WebSocketClient.concatenateStrings(first, second);

        assertEquals("Hello World", result);
    }
}