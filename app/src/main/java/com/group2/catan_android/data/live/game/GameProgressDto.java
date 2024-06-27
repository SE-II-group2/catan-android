package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;
import com.group2.catan_android.data.live.PlayerDto;

import java.util.List;

public class GameProgressDto extends MessageDto {
    public GameProgressDto() {
        super(MessageType.GAME_MOVE_NOTIFIER);
    }
    public GameProgressDto(GameMoveDto gameMoveDto) {
        super(MessageType.GAME_MOVE_NOTIFIER);
        this.gameMoveDto=gameMoveDto;

    }

    public GameMoveDto getGameMoveDto() {
        return gameMoveDto;
    }

    public void setGameMoveDto(GameMoveDto gameMoveDto) {
        this.gameMoveDto = gameMoveDto;
    }

    private GameMoveDto gameMoveDto;


}
