package com.group2.catan_android.data.live.game;


import com.group2.catan_android.gamelogic.enums.Location;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;


public class HexagonDto{
    private Location location;
    private ResourceDistribution resourceDistribution;
    private int value;
    private int id;

    public HexagonDto(Location location, ResourceDistribution resourceDistribution, int value, int id) {
        this.location = location;
        this.resourceDistribution = resourceDistribution;
        this.value = value;
        this.id = id;
    }

    public HexagonDto() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
