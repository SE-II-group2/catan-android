package com.group2.catan_android.data.live;

public class GameStartedDto extends MessageDto{
    public GameStartedDto(){
        setEventType(MessageType.GAME_STARTED);
    }
}
