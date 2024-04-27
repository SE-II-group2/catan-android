package com.group2.catan_android.networking.socket;

import android.hardware.ConsumerIrManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompMessage;

public class SocketManager {
    private static final String SOCKET_URI = "ws://10.0.2.2:8080/catan";
    private StompClient client;

    private Disposable liveCycleSubscriptions;
    private CompositeDisposable messageSubscriptions;

    private static final SocketManager instance = new SocketManager();

    public static SocketManager getInstance(){
        return instance;
    }

    public SocketManager(){
        messageSubscriptions = new CompositeDisposable();
    };

    public void reInit(){
        disconnect();
        messageSubscriptions = new CompositeDisposable();
    }

    public void connect(String token){
        if(isConnected()){
            disconnect();
        }
        Map<String, String> auth = Map.of("Authorization", token);
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URI, auth);
        client.connect();
    }

    public void onLifecycleEvent(Consumer<LifecycleEvent> consumer){
        if(liveCycleSubscriptions != null)
            liveCycleSubscriptions.dispose();
        liveCycleSubscriptions = client.lifecycle().subscribe(consumer);
    }

    public void unsubscribeAll(){
        messageSubscriptions.dispose();
    }

    public void disconnect(){
        if(client != null)
            client.disconnect();
        messageSubscriptions.dispose();
        messageSubscriptions.clear();
        if(liveCycleSubscriptions != null)
            liveCycleSubscriptions.dispose();
    }

    public void subscribe(String destination, Consumer<StompMessage> onMessage){
        messageSubscriptions.add(client.topic(destination).subscribe(onMessage));
    }
    public void subscribe(String destination, Consumer<StompMessage> onMessage, Consumer<Throwable> onError){
        messageSubscriptions.add(client.topic(destination).subscribe(onMessage, onError));
    }

    public boolean isConnected(){
        if(client != null)
            return client.isConnected();
        return false;
    }



}
