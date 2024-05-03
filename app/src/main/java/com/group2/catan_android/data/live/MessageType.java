package com.group2.catan_android.data.live;

/**
 * The types of Messages that can be sent.
 * No enum because easier for Jackson
 */

public interface MessageType {
    String PLAYERS_CHANGED = "PLAYERS_CHANGED";
    String GAME_STARTED = "GAME_STARTED";
}