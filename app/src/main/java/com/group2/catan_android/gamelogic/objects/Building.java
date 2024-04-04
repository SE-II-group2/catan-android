package com.group2.catan_android.gamelogic.objects;

public class Building extends Intersection {

    // Vorteile von type: Einfacher upgraden, einfach beim überprüfen muss nur nach building gecheckt werden und nicht village und city
    public enum BuildingType { CITY, VILLAGE }
    BuildingType type;

    public Building(int playerID, BuildingType type) {
        this.playerID = playerID;
        this.type = type;
    }

    public void giveResources(int[] resources) {
        if(this.type == BuildingType.CITY){
            for (int i = 0; i < resources.length; i++) {
                resources[i] *= 2; // Multiply the resources by 2 for cities
            }
        }
    }

    public BuildingType getType() {
        return type;
    }
}