package com.group2.catan_android.data.live.game;


import com.group2.catan_android.gamelogic.enums.Hexagontype;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;


public class HexagonDto{
    private Hexagontype hexagonType;
    private ResourceDistribution resourceDistribution;
    private int value;
    private int id;

    private boolean hasRobber;

    public HexagonDto(Hexagontype hexagonType, ResourceDistribution resourceDistribution, int value, int id, boolean hasRobber) {
        this.hexagonType = hexagonType;
        this.resourceDistribution = resourceDistribution;
        this.value = value;
        this.id = id;
        this.hasRobber=hasRobber;
    }

    public Hexagontype getHexagonType() {
        return hexagonType;
    }

    public void setHexagonType(Hexagontype hexagonType) {
        this.hexagonType = hexagonType;
    }

    public boolean isHasRobber() {
        return hasRobber;
    }

    public void setHasRobber(boolean hasRobber) {
        this.hasRobber = hasRobber;
    }
    public HexagonDto() {
    }

    public ResourceDistribution getResourceDistribution() {
        return resourceDistribution;
    }

    public void setResourceDistribution(ResourceDistribution resourceDistribution) {
        this.resourceDistribution = resourceDistribution;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
