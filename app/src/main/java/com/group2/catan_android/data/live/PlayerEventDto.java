package com.group2.catan_android.data.live;

/**
 * Describes an Event related to a player.
 */
public class PlayerEventDto {
    Type type;
    PlayerDto player;
    public enum Type {
        PLAYER_JOINED,
        PLAYER_LEFT,
        //TODO: Connection state changed. IE socket Disconnect
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public PlayerDto getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDto player) {
        this.player = player;
    }
}
