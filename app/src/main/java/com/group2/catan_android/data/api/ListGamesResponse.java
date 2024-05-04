package com.group2.catan_android.data.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.group2.catan_android.data.model.AvailableGame;

import java.util.List;

public class ListGamesResponse {
    @JsonProperty("count")
    private int count;
    @JsonProperty("gameList")
    List<AvailableGame> gameList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<AvailableGame> getGameList() {
        return gameList;
    }

    public void setGameList(List<AvailableGame> gameList) {
        this.gameList = gameList;
    }
}
