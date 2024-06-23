package com.group2.catan_android.data.live.game;


public class BuildVillageMoveDto extends GameMoveDto {
    public BuildVillageMoveDto(int intersectionID) {
        this.intersectionID = intersectionID;
        this.setEventType(GameMoveType.BUILDVILLAGEMOVE);
    }

    public BuildVillageMoveDto() {
        this.setEventType(GameMoveType.BUILDVILLAGEMOVE);
    }

    public int getIntersectionID() {
        return intersectionID;
    }

    public void setIntersectionID(int intersectionID) {
        this.intersectionID = intersectionID;
    }

    private int intersectionID;
}

