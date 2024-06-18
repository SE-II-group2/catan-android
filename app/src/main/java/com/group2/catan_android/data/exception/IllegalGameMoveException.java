package com.group2.catan_android.data.exception;

public class IllegalGameMoveException extends RuntimeException{
    public IllegalGameMoveException(String message){
        super(message);
    }
}
