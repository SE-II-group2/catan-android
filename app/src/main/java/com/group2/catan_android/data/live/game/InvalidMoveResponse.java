package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;

public class InvalidMoveResponse extends MessageDto {
    public InvalidMoveResponse(String message) {
        super(MessageType.INVALID_GAME_MOVE);
        this.message = message;
    }

    String message;

    public InvalidMoveResponse() {
        super(MessageType.INVALID_GAME_MOVE);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
