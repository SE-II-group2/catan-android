package com.group2.catan_android.networking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListGameResponse {
    @JsonProperty("count")
    private int count;
    @JsonProperty("gameList")
    List<Game> gameList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
    }
}
