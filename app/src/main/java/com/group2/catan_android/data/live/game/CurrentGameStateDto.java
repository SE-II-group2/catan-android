package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;
import com.group2.catan_android.data.live.PlayerDto;

import java.util.List;

public class CurrentGameStateDto extends MessageDto {
    public CurrentGameStateDto(List<HexagonDto> hexagons, List<IntersectionDto> intersections, List<ConnectionDto> connections, List<IngamePlayerDto> playerOrder, boolean isSetupPhase) {
        this.hexagons = hexagons;
        this.intersections=intersections;
        this.connections = connections;
        this.playerOrder=playerOrder;
        this.isSetupPhase=isSetupPhase;
        this.setEventType(MessageType.GAME_OBJECT);
    }

    public CurrentGameStateDto() {
        this.setEventType(MessageType.GAME_OBJECT);
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

    public List<IngamePlayerDto> getPlayerOrder() {
        return playerOrder;
    }

    public void setPlayerOrder(List<IngamePlayerDto> playerOrder) {
        this.playerOrder = playerOrder;
    }

    private List<HexagonDto> hexagons;
    private List<IntersectionDto> intersections;
    private List<ConnectionDto> connections;
    private List<IngamePlayerDto> playerOrder;

    public boolean isSetupPhase() {
        return isSetupPhase;
    }

    public void setSetupPhase(boolean setupPhase) {
        isSetupPhase = setupPhase;
    }

    private boolean isSetupPhase;
}



