package com.group2.catan_android.gamelogic.objects;

public abstract class Building {
    protected int playerID;

    public Building(int playerID) {
        this.playerID = playerID;
    }

    public abstract void giveResources(int[] resources);

    public int getPlayerID(){
        return this.playerID;
    }
}