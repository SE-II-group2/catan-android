package com.group2.catan_android.data.service;


import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

public class StompDriver {
    private static final String SOCKET_URL = "ws://10.0.2.2:8080/catan";
    private static StompDriver instance;

    private StompClient client;
    private StompDriver(){

    }
    public static StompDriver getInstance() {
        if(instance == null){
            instance = new StompDriver();
        }
        return instance;
    }

    public void connect(String token){
        if(isConnected()){
            client.disconnect();
        }
        Map<String, String> auth = Map.of("Authorization", token);
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL, auth);
        client.connect();
    }

    public Flowable<StompMessage> getTopic(String topic){
        return client.topic(topic);
    }

    private boolean isConnected(){
        return client != null && client.isConnected();
    }
}
