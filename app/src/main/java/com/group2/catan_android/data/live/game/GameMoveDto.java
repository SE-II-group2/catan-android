package com.group2.catan_android.data.live.game;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BuildRoadMoveDto.class, name = GameMoveType.BUILDROADMOVE),
        @JsonSubTypes.Type(value = BuildVillageMoveDto.class, name = GameMoveType.BUILDVILLAGEMOVE),
        @JsonSubTypes.Type(value = EndTurnMoveDto.class, name = GameMoveType.ENTTURNMOVE),
        @JsonSubTypes.Type(value = RollDiceDto.class, name = GameMoveType.ROLLDICEMOVE),
        @JsonSubTypes.Type(value= MoveRobberDto.class, name = GameMoveType.MOVEROBBERMOVE),
        @JsonSubTypes.Type(value = AccuseCheatingDto.class, name = GameMoveType.ACCUSECHEATINGMOVE),
        @JsonSubTypes.Type(value = MakeTradeOfferMoveDto.class, name = GameMoveType.MAKETRADEMOVE),
        @JsonSubTypes.Type(value = AcceptTradeOfferMoveDto.class, name = GameMoveType.ACCEPTTRADEMOVE)
})

//no longer abstract because this causes issues with serialization and deserialization
public abstract class GameMoveDto {
}

