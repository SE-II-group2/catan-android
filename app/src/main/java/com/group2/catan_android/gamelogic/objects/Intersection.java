package com.group2.catan_android.gamelogic.objects;

import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.BuildingType;

public class Intersection {
    Player player;
    BuildingType type = BuildingType.EMPTY;

    public Player getPlayer() {
        return player;
    }

    public BuildingType getType() {
        return type;
    }
}
