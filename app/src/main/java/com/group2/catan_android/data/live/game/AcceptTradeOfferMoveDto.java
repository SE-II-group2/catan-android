package com.group2.catan_android.data.live.game;
public class AcceptTradeOfferMoveDto extends GameMoveDto {
    public AcceptTradeOfferMoveDto() {
    }

    public AcceptTradeOfferMoveDto(TradeOfferDto tradeOfferDto) {
        this.tradeOfferDto = tradeOfferDto;
    }
    private TradeOfferDto tradeOfferDto;

    public TradeOfferDto getTradeOfferDto() {
        return tradeOfferDto;
    }

    public void setTradeOfferDto(TradeOfferDto tradeOfferDto) {
        this.tradeOfferDto = tradeOfferDto;
    }
}
