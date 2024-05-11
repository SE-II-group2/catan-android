package com.group2.catan_android.gamelogic;

import com.group2.catan_android.fragments.interfaces.ResourceUpdateListener;
import com.group2.catan_android.gamelogic.objects.ProgressCard;

import java.util.List;

public class Player {

    private final String token;
    private final String gameID;
    private final String displayName;
    private int victoryPoints = 0;
    private final int[] resources = new int[]{0,0,0,0,0};
    private final int color;

    private ResourceUpdateListener listener;

    private List<ProgressCard> progressCards;

    public void setResourceUpdateListener(ResourceUpdateListener listener) {
        this.listener = listener;
    }

    public Player(String token, String displayName, String gameID, int color) {
        this.token = token;
        this.displayName = displayName;
        this.gameID = gameID;
        this.color = color;
    }

    public void adjustResources(int[] resources) {
        if (resources != null && resources.length == 5) {
            for (int i = 0; i < resources.length; i++) {
                this.resources[i] += resources[i];
            }

            if (listener != null) {
                listener.onResourcesUpdated(this.resources);
            } else {
                throw new IllegalArgumentException("No listener found");
            }
        } else {
            throw new IllegalArgumentException("Resources array must be not-null and have exactly 5 elements.");
        }

    }

    public boolean resourcesSufficient(int[] resourceCost){

        if(resourceCost != null && resourceCost.length == 5){
            for (int i = 0; i < resourceCost.length; i++) {
                if(this.resources[i] + resourceCost[i] < 0){
                    return false;
                }
            }
            return true;
        } else {
            throw new IllegalArgumentException("Resources Cost array must be not-null and have exactly 5 elements.");
        }

    }

    public void increaseVictoryPoints(int amount){
        victoryPoints+=amount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }

    public int[] getResources() {
        return resources;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }
}
