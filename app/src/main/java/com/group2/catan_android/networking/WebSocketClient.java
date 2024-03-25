package com.group2.catan_android.networking;

import android.util.Log;

import java.util.Date;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;



public class WebSocketClient {

    // TODO use correct hostname:port
    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * https://developer.android.com/studio/run/emulator-networking
     */
    private final String WEBSOCKET_URI = "ws://10.0.2.2:8080/websocket-example-broker";

    private  CompositeDisposable compositeDisposable = new CompositeDisposable();
    private StompClient client;

    public void connectToServer(WebSocketMessageHandler<String> messageHandler) {
        resetSubscriptions();
        if(client != null && client.isConnected())
            client.disconnect();

        if (messageHandler == null)
            throw new IllegalArgumentException("messageHandler is required");

        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URI);

        compositeDisposable.add(client.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()){
                case OPENED:
                    Log.d("network", "Stomp connection opened");
                    break;
                case ERROR:
                    Log.d("network", "Stomp connection Error: " + lifecycleEvent.getException().getMessage());
                    break;
                case CLOSED:
                    Log.d("network", "Stomp connection closed");
                    break;
                case FAILED_SERVER_HEARTBEAT:
                    Log.d("network", "Stomp Failed server heartbeat");
                    break;
            }
        }));

        Disposable topic = client.topic("/topic/hello-response")
                .subscribe(message -> {
                    Log.d("Communication" ,"Received " + message.getPayload());
                    messageHandler.onMessageReceived(message.getPayload());
                }, throwable -> {
                    Log.e("Communication", "Error communicating", throwable);
                });

        compositeDisposable.add(topic);

        client.connect();
    }

    public void sendMessageToServer(String message){
        if(!client.isConnected()){
            Log.d("Communication", "not Connected");
        }
        compositeDisposable.add(client.send ("/app/hello", "Hello @")
                        .subscribe(()-> {
                            Log.d("communication", "hello sent");
                        }, throwable -> {
                            Log.e("Communication", "Failure", throwable);
                        }));
    }

    public void resetSubscriptions(){
        if(compositeDisposable != null){
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    // Simple method to demonstrate unit testing and test coverage with sonarcloud
    public static String concatenateStrings(String first, String second) {
        return first + " " + second;
    }
}
