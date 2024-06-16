package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;

public class TradeOfferDto extends MessageDto{
    public TradeOfferDto() {
        this.setEventType(MessageType.PLAYER_NOTIFY);
    }
    public TradeOfferDto(int[] tradeMove_getResources, int[] tradeMove_giveResources, int playerID) {
        this.getResources=tradeMove_giveResources;
        this.giveResources=tradeMove_getResources;
        this.playerID = playerID;
        this.setEventType(MessageType.PLAYER_NOTIFY);
    }
    //order swapped!!!
    private int[] getResources;
    private int[] giveResources;
    private int playerID;

    public int[] getGetResources() {
        return getResources;
    }

    public void setGetResources(int[] getResources) {
        this.getResources = getResources;
    }

    public int[] getGiveResources() {
        return giveResources;
    }

    public void setGiveResources(int[] giveResources) {
        this.giveResources = giveResources;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
}
