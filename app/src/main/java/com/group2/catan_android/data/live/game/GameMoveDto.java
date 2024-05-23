package com.group2.catan_android.data.live.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// fixme see backend comments
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BuildRoadMoveDto.class, name = GameMoveType.BUILDROADMOVE),
        @JsonSubTypes.Type(value = BuildVillageMoveDto.class, name = GameMoveType.BUILDVILLAGEMOVE),
        @JsonSubTypes.Type(value = EndTurnMoveDto.class, name = GameMoveType.ENTTURNMOVE),
        @JsonSubTypes.Type(value = RollDiceDto.class, name = GameMoveType.ROLLDICEMOVE),
})

//no longer abstract because this causes issues with serialization and deserialization
public class GameMoveDto {
    private String eventType;

    public GameMoveDto(String eventType) {
        this.eventType = eventType;
    }

    public GameMoveDto() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}

