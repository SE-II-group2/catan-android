package com.group2.catan_android.gamelogic.objects;

import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.gamelogic.enums.ResourceCost;

public class ProgressCard {

    Player player;
    ProgressCardType type;

    ResourceCost cost = ResourceCost.PROGRESS_CARD;

    public ProgressCard(ProgressCardType type, ResourceCost cost) {
        this.type = type;
        this.cost = cost;
    }

    public void use(){
        switch (type){
            case KNIGHT:
                // set Knight to different Hexagon and steal 1 Resource from someone who has built on that hexagon
                break;

            case YEAR_OF_PLENTY:
                // free choice to pick 2 resources from bank (can also be the same)
                break;

            case ROAD_BUILDING:
                // build to roads for free
                break;

            case MONOPOLY:
                // choose one resource and get all resources from that type from every player
                break;

            case VICTORY_POINT:
                this.player.increaseVictoryPoints(1);
                break;
        }
    }

}
