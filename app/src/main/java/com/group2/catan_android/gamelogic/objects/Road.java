package com.group2.catan_android.gamelogic.objects;

public class Road extends Connection{
    private int playerId;

    public Road(int playerId){
        this.playerId=playerId;
    }

    public int getPlayerId(){
        return playerId;
    }
}
