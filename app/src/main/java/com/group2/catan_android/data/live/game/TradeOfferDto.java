package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;

public class TradeOfferDto extends MessageDto{
    public TradeOfferDto() {
        super(MessageType.TRADE_OFFERED);
    }
    public TradeOfferDto(int[] tradeMove_getResources, int[] tradeMove_giveResources, IngamePlayerDto fromPlayer) {
        super(MessageType.TRADE_OFFERED);
        this.getResources=tradeMove_giveResources;
        this.giveResources=tradeMove_getResources;
        this.fromPlayer = fromPlayer;
    }
    //order swapped!!!
    private int[] getResources;
    private int[] giveResources;
    private IngamePlayerDto fromPlayer;

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

    public IngamePlayerDto getFromPlayer() {
        return fromPlayer;
    }

    public void setFromPlayer(IngamePlayerDto fromPlayer) {
        this.fromPlayer = fromPlayer;
    }
}
