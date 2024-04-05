package com.group2.catan_android.networking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Game {
    @JsonProperty("gameID")
    private String gameID;
    @JsonProperty("playerCount")
    private int playerCount;

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
