package com.group2.catan_android.data.live;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.group2.catan_android.data.live.game.CurrentGameStateDto;
import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.live.game.GameOverDto;
import com.group2.catan_android.data.live.game.TradeOfferDto;

// fixme see backend comments
/**
 * Defines the Basic Structure of a Stomp Message to the client
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlayersInLobbyDto.class, name = MessageType.PLAYERS_CHANGED),
        @JsonSubTypes.Type(value = GameStartedDto.class, name = MessageType.GAME_STARTED),
        @JsonSubTypes.Type(value = CurrentGameStateDto.class, name = MessageType.GAME_OBJECT),
        @JsonSubTypes.Type(value = GameProgressDto.class, name = MessageType.GAME_MOVE_NOTIFIER),
        @JsonSubTypes.Type(value = GameOverDto.class, name = MessageType.GAME_OVER),
        @JsonSubTypes.Type(value = TradeOfferDto.class, name = MessageType.PLAYER_NOTIFY)
})

public abstract class MessageDto {
    private String eventType;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}

