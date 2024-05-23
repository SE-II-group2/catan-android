package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.game.IngamePlayerDto;


public class IntersectionDto {
    private IngamePlayerDto owner;
    private String BuildingType;
    private int id;

    public IntersectionDto(IngamePlayerDto owner, String buildingType, int id) {
        this.owner = owner;
        BuildingType = buildingType;
        this.id = id;
    }

    public IntersectionDto() {
    }

    public IngamePlayerDto getOwner() {
        return owner;
    }

    public void setOwner(IngamePlayerDto owner) {
        this.owner = owner;
    }

    public String getBuildingType() {
        return BuildingType;
    }

    public void setBuildingType(String buildingType) {
        BuildingType = buildingType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
