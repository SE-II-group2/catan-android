package com.group2.catan_android.data.live.game;




public class RollDiceDto extends GameMoveDto{
    public RollDiceDto(int diceRoll) {
        this.diceRoll = diceRoll;
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
    private int diceRoll;

    public RollDiceDto() {
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
}

