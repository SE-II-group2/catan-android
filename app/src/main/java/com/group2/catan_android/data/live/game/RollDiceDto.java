package com.group2.catan_android.data.live.game;


public class RollDiceDto extends GameMoveDto{
    public RollDiceDto(int diceRoll) {
        this.diceRoll = diceRoll;
    }

    public int getDiceRoll() {
        return diceRoll;
    }

    public void setDiceRoll(int diceRoll) {
        this.diceRoll = diceRoll;
    }

    private int diceRoll;

    public RollDiceDto() {
    }
}

