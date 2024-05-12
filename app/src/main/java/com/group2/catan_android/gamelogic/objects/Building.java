package com.group2.catan_android.gamelogic.objects;

import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.BuildingType;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;

public class Building extends Intersection {

    Player player;
    int id;
    BuildingType type;

    public Building(Player player, BuildingType type, int id) {
        this.player = player;
        this.type = type;
        this.id = id;
    }

    public void giveResources(ResourceDistribution distribution) {
        int[] resources = distribution.getDistribution();
        if(this.type == BuildingType.CITY){
            for (int i = 0; i < resources.length; i++) {
                resources[i] *= 2; // Multiply the resources by 2 for cities
            }
        }
        player.adjustResources(resources);
    }

    public BuildingType getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    public int getId() {
        return id;
    }
}