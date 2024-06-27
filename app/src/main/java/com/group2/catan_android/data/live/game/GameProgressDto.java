package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;
import com.group2.catan_android.data.live.PlayerDto;

import java.util.List;

public class GameProgressDto extends MessageDto {
    public GameProgressDto() {
    }
    public GameProgressDto(GameMoveDto gameMoveDto) {
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
