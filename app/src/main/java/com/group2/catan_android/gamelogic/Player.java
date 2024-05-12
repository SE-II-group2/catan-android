package com.group2.catan_android.gamelogic;

import com.group2.catan_android.data.live.PlayerDto;
import com.group2.catan_android.fragments.interfaces.ResourceUpdateListener;

public class Player {


    private final String displayName;
    private int victoryPoints = 0;
    private int[] resources = new int[]{0,0,0,0,0};
    private final int color;
    private String gameID;
    private String token;

    private ResourceUpdateListener listener;

    public void setResourceUpdateListener(ResourceUpdateListener listener) {
        this.listener = listener;
    }
    public Player( String token, String displayName, String gameID, int color) {
        this.token=token;
        this.displayName=displayName;
        this.gameID=gameID;
        this.color = color;
    }

    public Player( String displayName,int victoryPoints, int[] resources,  int color) {
        this.displayName = displayName;
        this.victoryPoints=victoryPoints;
        this.resources=resources;
        this.color = color;
    }

    public void adjustResources(int[] resources) {
        if (resources != null && resources.length == 5) {
            for (int i = 0; i < resources.length; i++) {
                this.resources[i] += resources[i];
            }

            if (listener != null) {
                listener.onResourcesUpdated(this.resources);
            }
        }

    }

    public boolean resourcesSufficient(int[] resourceCost){

        if(resourceCost != null && resourceCost.length == 5){
            for (int i = 0; i < resourceCost.length; i++) {
                if(this.resources[i] + resourceCost[i] < 0){
                    return false;
                }
            }
        }
        return true;
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
