package com.group2.catan_android.data.live.game;

public class AccuseCheatingDto extends GameMoveDto{
    public AccuseCheatingDto() {
        this.setEventType(GameMoveType.ACCUSECHEATINGMOVE);
    }
}

