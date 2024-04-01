package com.group2.catan_android.gamelogic.objects;

public class Village extends Building {
    public Village(int playerID) {
        super(playerID);
    }

    @Override
    public void giveResources(int[] resources) {
        // Villages do not have extra functionality for getResources method
    }
}
