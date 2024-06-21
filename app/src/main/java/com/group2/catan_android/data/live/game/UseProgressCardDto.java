package com.group2.catan_android.data.live.game;

import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;

import java.util.List;

public class UseProgressCardDto extends GameMoveDto {

    public UseProgressCardDto(ProgressCardType progressCardType, List<ResourceDistribution> chosenResources, ResourceDistribution monopolyResource, int hexagonID) {
        this.progressCardType = progressCardType;
        this.chosenResources = chosenResources;
        this.monopolyResource = monopolyResource;
        this.hexagonID = hexagonID;
        setEventType(GameMoveType.USEPROGRESSCARD);
    }
    public void setProgressCardType(ProgressCardType progressCardType){
        this.progressCardType = progressCardType;
    }
    public void setChosenResources(List<ResourceDistribution> chosenResources){
        this.chosenResources = chosenResources;
    }
    public void setMonopolyResource(ResourceDistribution monopolyResource){
        this.monopolyResource = monopolyResource;
    }
    public ProgressCardType getProgressCardType(){
        return progressCardType;
    }

    public List<ResourceDistribution> getChosenResources() {
        return chosenResources;
    }

    public ResourceDistribution getMonopolyResource() {
        return monopolyResource;
    }
    public int getHexagonID(){
        return hexagonID;
    }

    private ProgressCardType progressCardType;
    private List<ResourceDistribution> chosenResources;
    private ResourceDistribution monopolyResource;
    private int hexagonID;
}
