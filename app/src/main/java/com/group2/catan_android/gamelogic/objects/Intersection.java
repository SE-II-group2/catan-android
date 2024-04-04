package com.group2.catan_android.gamelogic.objects;

public class Intersection {
    int playerID;

    public enum BuildingType { CITY, VILLAGE }

    public int getPlayerID(){
        return playerID;
    }
}
