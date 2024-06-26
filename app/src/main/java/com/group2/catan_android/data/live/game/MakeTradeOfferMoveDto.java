package com.group2.catan_android.data.live.game;

import java.util.List;

public class MakeTradeOfferMoveDto extends GameMoveDto {
    public MakeTradeOfferMoveDto() {
        this.setEventType(GameMoveType.MAKETRADEMOVE);
    }

    public MakeTradeOfferMoveDto(int[] giveResources, int[] getResources, List<Integer> toPlayers) {
        this.giveResources = giveResources;
        this.getResources = getResources;
        this.toPlayers = toPlayers;
        this.setEventType(GameMoveType.MAKETRADEMOVE);
    }

    private int[] giveResources;
    private int[] getResources;
    private List<Integer> toPlayers;

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

    public List<Integer> getToPlayers() {
        return toPlayers;
    }

    public void setToPlayers(List<Integer> toPlayers) {
        this.toPlayers = toPlayers;
    }
}
