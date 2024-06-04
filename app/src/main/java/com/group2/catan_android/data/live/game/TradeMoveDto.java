package com.group2.catan_android.data.live.game;
public class TradeMoveDto extends GameMoveDto {
    public TradeMoveDto(int[] resources, boolean[] toPlayer, int waitTime) {
        this.resources = resources;
        this.toPlayer = toPlayer;
        this.waitTime = waitTime;
        this.setEventType(GameMoveType.TRADEMOVE);
    }

    private int[] resources;
    private boolean[] toPlayer;
    private int waitTime;

    public int[] getResources() {
        return resources;
    }

    public void setResources(int[] resources) {
        this.resources = resources;
    }

    public boolean[] getToPlayer() {
        return toPlayer;
    }

    public void setToPlayer(boolean[] toPlayer) {
        this.toPlayer = toPlayer;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
}

