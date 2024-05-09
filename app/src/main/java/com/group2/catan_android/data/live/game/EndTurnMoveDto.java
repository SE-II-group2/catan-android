package com.group2.catan_android.data.live.game;



public class EndTurnMoveDto extends GameMoveDto{

    public EndTurnMoveDto() {
        this.setEventType(GameMoveType.ENTTURNMOVE);
    }
}
