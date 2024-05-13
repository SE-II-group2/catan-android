package com.group2.catan_android.data.live.game;

public class BuildRoadMoveDto extends GameMoveDto{

    public BuildRoadMoveDto(int connectionID) {
        this.connectionID = connectionID;
        this.setEventType(GameMoveType.BUILDROADMOVE);
    }

    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public BuildRoadMoveDto() {
        this.setEventType(GameMoveType.BUILDROADMOVE);
    }

    private int connectionID;
}

