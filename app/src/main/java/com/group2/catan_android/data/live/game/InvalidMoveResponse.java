package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.live.MessageType;

public class InvalidMoveResponse extends MessageDto {
    public InvalidMoveResponse(String message) {
        this.message = message;
    }

    String message;

    public InvalidMoveResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
