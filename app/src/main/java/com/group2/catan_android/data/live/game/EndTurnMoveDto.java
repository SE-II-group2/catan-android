package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.PlayerDto;

import java.util.ArrayList;

public class EndTurnMoveDto extends GameMoveDto{


    public EndTurnMoveDto() {
        this.setEventType(GameMoveType.ENTTURNMOVE);
    }

}
