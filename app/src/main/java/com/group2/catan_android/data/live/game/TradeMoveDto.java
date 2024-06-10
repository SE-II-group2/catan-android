package com.group2.catan_android.data.live.game;
public class TradeMoveDto extends GameMoveDto {
    public TradeMoveDto() {
        this.setEventType(GameMoveType.TRADEMOVE);
    }

    public TradeMoveDto(int[] giveResources, int[] getResources, boolean[] toPlayer) {
        this.giveResources = giveResources;
        this.getResources = getResources;
        this.toPlayer = toPlayer;
        this.setEventType(GameMoveType.TRADEMOVE);
    }

    private int[] giveResources;
    private int[] getResources;
    private boolean[] toPlayer;

    public int[] getGiveResources() {
        return giveResources;
    }

    public void setGiveResources(int[] giveResources) {
        this.giveResources = giveResources;
    }

    public int[] getGetResources() {
        return getResources;
    }

    public void setGetResources(int[] getResources) {
        this.getResources = getResources;
    }

    public boolean[] getToPlayer() {
        return toPlayer;
    }

    public void setToPlayer(boolean[] toPlayer) {
        this.toPlayer = toPlayer;
    }
}
