package com.group2.catan_android.gamelogic.objects;

import com.group2.catan_android.gamelogic.Player;

public class Road extends Connection{

    int id;

    public Road(Player player, int id){
        this.player = player ;
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
