package com.group2.catan_android.data.live.game;

public class BuildRoadMoveDto extends GameMoveDto{

    public BuildRoadMoveDto(int connectionID) {
        this.connectionID = connectionID;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public BuildRoadMoveDto() {
    }

    private int connectionID;
}

