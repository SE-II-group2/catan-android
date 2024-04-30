package com.group2.catan_android.gamelogic.objects;

import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.BuildingType;

public class Building extends Intersection {

    Player player;
    BuildingType type;

    public Building(Player player, BuildingType type) {
        this.player = player;
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