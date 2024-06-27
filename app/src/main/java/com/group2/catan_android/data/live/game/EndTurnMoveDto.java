package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.PlayerDto;

import java.util.ArrayList;

public class EndTurnMoveDto extends GameMoveDto{


    public EndTurnMoveDto() {
    }
    public EndTurnMoveDto(IngamePlayerDto nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public IngamePlayerDto getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(IngamePlayerDto nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    private IngamePlayerDto nextPlayer;
}
