package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;
import com.group2.catan_android.data.live.PlayerDto;

public class GameoverDto extends MessageDto {

    private PlayerDto winner;

    public GameoverDto(PlayerDto winner) {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
        this.winner=winner;
    }

    public PlayerDto getWinner() {
        return winner;
    }

    public void setWinner(PlayerDto winner) {
        this.winner = winner;
    }

    public GameoverDto() {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
    }
}
