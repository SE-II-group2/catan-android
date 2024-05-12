package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.PlayerDto;

import java.util.ArrayList;

public class EndTurnMoveDto extends GameMoveDto{

    public ArrayList<PlayerDto> getTurnOder() {
        return turnOder;
    }

    public void setTurnOder(ArrayList<PlayerDto> turnOder) {
        this.turnOder = turnOder;
    }

    private ArrayList<PlayerDto> turnOder;
    public EndTurnMoveDto() {
        this.setEventType(GameMoveType.ENTTURNMOVE);
    }

    public EndTurnMoveDto(ArrayList<PlayerDto> turnOder) {
        this.setEventType(GameMoveType.ENTTURNMOVE);
        this.turnOder=turnOder;
    }
}
