package com.group2.catan_android.data.live.game;

public class IngamePlayerDto {
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int[] getResources() {
        return resources;
    }

    public void setResources(int[] resources) {
        this.resources = resources;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    public int getInGameID() {
        return inGameID;
    }

    public void setInGameID(int inGameID) {
        this.inGameID = inGameID;
    }

    public IngamePlayerDto(String displayName, int[] resources, int victoryPoints, int color, int inGameID) {
        this.displayName = displayName;
        this.resources = resources;
        this.victoryPoints = victoryPoints;
        this.color = color;
        this.inGameID = inGameID;
    }

    public IngamePlayerDto() {
    }

    private String displayName;
    private int[] resources;
    private int victoryPoints;
    private int color;
    private int inGameID;
}

