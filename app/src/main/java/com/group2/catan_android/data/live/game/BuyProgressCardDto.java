package com.group2.catan_android.data.live.game;

public class BuyProgressCardDto extends GameMoveDto {
    public BuyProgressCardDto(){
        this.setEventType(GameMoveType.BUYPROGRESSCARD);
    }
}
