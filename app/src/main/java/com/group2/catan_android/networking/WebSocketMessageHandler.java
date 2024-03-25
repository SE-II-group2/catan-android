package com.group2.catan_android.networking;

public interface WebSocketMessageHandler<T> {
    void onMessageReceived(T message);
}
