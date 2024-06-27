package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;

import java.util.List;

public class CurrentGameStateDto extends MessageDto {
    public CurrentGameStateDto(List<HexagonDto> hexagons, List<IntersectionDto> intersections, List<ConnectionDto> connections, List<IngamePlayerDto> players, IngamePlayerDto activePlayer,  boolean isSetupPhase) {
        super(MessageType.GAME_OBJECT);
        this.hexagons = hexagons;
        this.intersections=intersections;
        this.connections = connections;
        this.players=players;
        this.isSetupPhase=isSetupPhase;
        this.activePlayer = activePlayer;
    }

    public CurrentGameStateDto() {
        super(MessageType.GAME_OBJECT);
    }

    public List<HexagonDto> getHexagons() {
        return hexagons;
    }

    public void setHexagons(List<HexagonDto> hexagons) {
        this.hexagons = hexagons;
    }

    public List<IntersectionDto> getIntersections() {
        return intersections;
    }

    public void setIntersections(List<IntersectionDto> intersections) {
        this.intersections = intersections;
    }

    public List<ConnectionDto> getConnections() {
        return connections;
    }

    public void setConnections(List<ConnectionDto> connections) {
        this.connections = connections;
    }

    public List<IngamePlayerDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<IngamePlayerDto> players) {
        this.players = players;
    }

    private List<HexagonDto> hexagons;
    private List<IntersectionDto> intersections;
    private List<ConnectionDto> connections;
    private List<IngamePlayerDto> players;
    private IngamePlayerDto activePlayer;

    public IngamePlayerDto getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(IngamePlayerDto activePlayer) {
        this.activePlayer = activePlayer;
    }

    public boolean isSetupPhase() {
        return isSetupPhase;
    }

    public void setSetupPhase(boolean setupPhase) {
        isSetupPhase = setupPhase;
    }

    private boolean isSetupPhase;
}



