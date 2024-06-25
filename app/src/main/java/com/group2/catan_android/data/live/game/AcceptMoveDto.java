package com.group2.catan_android.data.live.game;
public class AcceptMoveDto extends GameMoveDto {
    public AcceptMoveDto() {
        this.setEventType(GameMoveType.ACCEPTMOVE);
    }

    public AcceptMoveDto(TradeOfferDto tradeOfferDto) {
        this.tradeOfferDto = tradeOfferDto;
        this.setEventType(GameMoveType.ACCEPTMOVE);
    }
    private TradeOfferDto tradeOfferDto;

    public TradeOfferDto getTradeOfferDto() {
        return tradeOfferDto;
    }

    public void setTradeOfferDto(TradeOfferDto tradeOfferDto) {
        this.tradeOfferDto = tradeOfferDto;
    }
}
