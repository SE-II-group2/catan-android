package com.group2.catan_android.data.live;

import java.util.List;

public class PlayersInLobbyDto extends MessageDto {
    List<PlayerDto> players;
    PlayerDto admin;
    public List<PlayerDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDto> players) {
        this.players = players;
    }

    public PlayerDto getAdmin() {
        return admin;
    }

    public void setAdmin(PlayerDto admin) {
        this.admin = admin;
    }
}
