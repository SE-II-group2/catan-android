package com.group2.catan_android.data.live.game;

public class AccuseCheatingDto extends GameMoveDto {
    public AccuseCheatingDto() {
        this.setEventType(GameMoveType.ACCUSECHEATINGMOVE);
    }

    public AccuseCheatingDto(IngamePlayerDto sender) {
        this.sender = sender;
        this.setEventType(GameMoveType.ACCUSECHEATINGMOVE);
    }

    public IngamePlayerDto getSender() {
        return sender;
    }

    public void setSender(IngamePlayerDto sender) {
        this.sender = sender;
    }

    private IngamePlayerDto sender;
}

