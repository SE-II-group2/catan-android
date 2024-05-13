package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;
import com.group2.catan_android.data.live.PlayerDto;

import java.util.List;

public class GameProgressDto extends MessageDto {
    public GameProgressDto() {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
    }

    public GameProgressDto(List<IngamePlayerDto> players) {
        this.setEventType(MessageType.GAME_MOVE_NOTIFIER);
        this.players = players;

    }

    public List<IngamePlayerDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<IngamePlayerDto> players) {
        this.players = players;
    }

    private List<IngamePlayerDto> players;

}
