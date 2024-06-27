package com.group2.catan_android.data.live.game;

public class AccuseCheatingDto extends GameMoveDto {
    public AccuseCheatingDto() {

    }

    public AccuseCheatingDto(IngamePlayerDto sender) {
        this.sender = sender;
    }

    public IngamePlayerDto getSender() {
        return sender;
    }

    public void setSender(IngamePlayerDto sender) {
        this.sender = sender;
    }

    private IngamePlayerDto sender;
}

