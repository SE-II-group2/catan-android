package com.group2.catan_android.data.live.game;

public class MoveRobberDto extends GameMoveDto{

    public MoveRobberDto(int hexagonID, boolean legal) {
        this.setEventType(GameMoveType.MOVEROBBERMOVE);
        this.hexagonID=hexagonID;
        this.legal=legal;
    }

    public MoveRobberDto(){
        this.setEventType(GameMoveType.MOVEROBBERMOVE);
    }

    public int getHexagonID() {
        return hexagonID;
    }

    public void setHexagonID(int hexagonID) {
        this.hexagonID = hexagonID;
    }

    public boolean isLegal() {
        return legal;
    }

    public void setLegal(boolean legal) {
        this.legal = legal;
    }

    private int hexagonID;
    private boolean legal;
}
