package com.group2.catan_android.gamelogic;

import com.group2.catan_android.data.live.game.IngamePlayerDto;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.gamelogic.objects.ProgressCard;

import java.util.List;

public class Player {

    private String token;
    private String gameID;
    private int inGameID;
    private String displayName;
    private int victoryPoints = 0;
    private int[] resources = new int[]{0,0,0,0,0};
    private int color;

    private List<ProgressCardType> progressCards;

    public Player(String token, String displayName, String gameID, int color)  {
        this.token = token;
        this.displayName = displayName;
        this.gameID = gameID;
        this.color = color;
    }

    public Player (){}
    public Player( String displayName, int victoryPoints, int[] resources,  int color, List<ProgressCardType> progressCards) {
        this.displayName = displayName;
        this.victoryPoints=victoryPoints;
        this.resources=resources;
        this.color = color;
        this.progressCards = progressCards;
    }

    public void adjustResources(int[] resources) {
        if (resources != null && resources.length == 5) {
            for (int i = 0; i < resources.length; i++) {
                this.resources[i] += resources[i];
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
    public int getInGameID() {
        return inGameID;
    }
    public void setInGameID(int inGameID) {
        this.inGameID = inGameID;
    }
    public List<ProgressCardType> getProgressCards(){
        return progressCards;
    }

    public IngamePlayerDto toIngamePlayerDto(){
        return new IngamePlayerDto(this.displayName, this.resources, this.victoryPoints, this.color, this.inGameID, this.progressCards);
    }
}
