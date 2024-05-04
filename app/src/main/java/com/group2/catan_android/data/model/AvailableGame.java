package com.group2.catan_android.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvailableGame {
    @JsonProperty("gameID")
    private String gameID;
    @JsonProperty("playerCount")
    private int playerCount;

    public AvailableGame(String gameID, int playerCount){
        this.gameID = gameID;
        this.playerCount = playerCount;
    }

    public AvailableGame(){}
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

    @Override
    public String toString() {
        return "AvailableGame{" +
                "gameID='" + gameID + '\'' +
                ", playerCount=" + playerCount +
                '}';
    }
}
