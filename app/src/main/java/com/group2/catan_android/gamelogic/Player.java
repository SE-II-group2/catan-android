package com.group2.catan_android.gamelogic;

public class Player {

    private final String token;
    private final String displayName;
    private int victoryPoints = 0;
    private final int[] resources = new int[]{0,0,0,0,0};
    private final String color;

    public Player(String token, String displayName, String color, int victoryPoints) {
        this.token = token;
        this.displayName = displayName;
        this.color = color;
        this.victoryPoints = victoryPoints;
    }

    public void adjustResources(int[] resources){
        if(resources!=null&&resources.length == 5){
            for (int i = 0; i < resources.length; i++) {
                this.resources[i]+= resources[i];
            }
        }
    }

    public boolean resourcesSufficient(int[] resourceCost){
        if(resourceCost!=null&&resourceCost.length == 5){
            for (int i = 0; i < resourceCost.length; i++) {
                if(this.resources[i]+resourceCost[i]<0)return false;
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

    public String getColor() {
        return color;
    }

    public String getToken() {
        return token;
    }
}
