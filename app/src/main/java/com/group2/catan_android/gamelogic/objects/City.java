package com.group2.catan_android.gamelogic.objects;

public class City extends Building {
    public City(int playerID) {
        super(playerID);
    }

    @Override
    public void giveResources(int[] resources) {
        for (int i = 0; i < resources.length; i++) {
            resources[i] *= 2; // Multiply the resources by 2 for cities
        }
    }
}
