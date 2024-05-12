package com.group2.catan_android.data.live.game;


import java.util.HashMap;

public class RollDiceDto extends GameMoveDto{
    public RollDiceDto(int diceRoll) {
        this.diceRoll = diceRoll;
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }

    public int getDiceRoll() {
        return diceRoll;
    }

    public void setDiceRoll(int diceRoll) {
        this.diceRoll = diceRoll;
    }

    private int diceRoll;

    public RollDiceDto(int diceRoll, HashMap<String, int[]> playerResources) {
        this.diceRoll = diceRoll;
        this.playerResources=playerResources;
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }

    public HashMap<String, int[]> getPlayerResources() {
        return playerResources;
    }

    public void setPlayerResources(HashMap<String, int[]> playerResources) {
        this.playerResources = playerResources;
    }

    private HashMap<String, int[]> playerResources;
    public RollDiceDto() {
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
}

