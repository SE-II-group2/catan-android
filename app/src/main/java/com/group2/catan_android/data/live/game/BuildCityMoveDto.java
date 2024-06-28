package com.group2.catan_android.data.live.game;

public class BuildCityMoveDto extends GameMoveDto{
    public BuildCityMoveDto(int intersectionID) {
        this.intersectionID = intersectionID;
    }

    public BuildCityMoveDto() {}

    public int getIntersectionID() {
        return intersectionID;
    }

    public void setIntersectionID(int intersectionID) {
        this.intersectionID = intersectionID;
    }

    private int intersectionID;
}

