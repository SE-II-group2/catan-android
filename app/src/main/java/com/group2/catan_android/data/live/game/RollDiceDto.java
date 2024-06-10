package com.group2.catan_android.data.live.game;


public class RollDiceDto extends GameMoveDto{
    public RollDiceDto(int diceRoll) {
        this.diceRoll = diceRoll;
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
    public RollDiceDto(int diceRoll, MoveRobberDto moveRobberDto) {
        this.diceRoll = diceRoll;
        this.moveRobberDto = moveRobberDto;
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
    public int getDiceRoll() {
        return diceRoll;
    }

    public void setDiceRoll(int diceRoll) {
        this.diceRoll = diceRoll;
    }

    private int diceRoll;

    public MoveRobberDto getMoveRobberDto() {
        return moveRobberDto;
    }

    public void setMoveRobberDto(MoveRobberDto moveRobberDto) {
        this.moveRobberDto = moveRobberDto;
    }

    private MoveRobberDto moveRobberDto;

    public RollDiceDto() {
        this.setEventType(GameMoveType.ROLLDICEMOVE);
    }
}

