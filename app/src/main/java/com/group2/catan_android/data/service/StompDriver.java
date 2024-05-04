package com.group2.catan_android.data.service;


import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
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

    public Completable connect(String token){
        if(isConnected()){
            client.disconnect();
        }
        Map<String, String> auth = Map.of("Authorization", token);
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL, auth);
        client.connect();
        return Completable.create(emitter -> {
            Disposable d = lifecycle()
                    .take(1)
                    .subscribe(lifecycleEvent -> {
                        if(lifecycleEvent.getType() == LifecycleEvent.Type.OPENED){
                            emitter.onComplete();
                        }else{
                            emitter.onError(new Throwable("Failed to establish a Connection"));
                        }
                    });
            emitter.setDisposable(d);
        });
    }
    public Flowable<LifecycleEvent> lifecycle(){
        if(client == null)
            return Flowable.error(new NullPointerException());
        return client.lifecycle();
    }
    public void disconnect(){
        if(isConnected())
            client.disconnect();
    }

    public Flowable<StompMessage> getTopic(String topic){
        return client.topic(topic);
    }

    public boolean isConnected(){
        return client != null && client.isConnected();
    }
}
