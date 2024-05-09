package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;
import com.group2.catan_android.data.live.PlayerDto;

public class GameProgressDto extends MessageDto {
    public GameProgressDto() {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
    }
    public GameProgressDto(GameMoveDto moveDto, PlayerDto playerDto) {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
        this.moveDto = moveDto;
        this.playerDto = playerDto;

    }

    private GameMoveDto moveDto;
    private PlayerDto playerDto;

    public GameMoveDto getMoveDto() {
        return moveDto;
    }

    public void setMoveDto(GameMoveDto moveDto) {
        this.moveDto = moveDto;
    }

    public PlayerDto getPlayerDto() {
        return playerDto;
    }

    public void setPlayerDto(PlayerDto playerDto) {
        this.playerDto = playerDto;
    }
}
