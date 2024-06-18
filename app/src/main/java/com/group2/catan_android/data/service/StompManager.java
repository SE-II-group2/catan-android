package com.group2.catan_android.data.service;

import android.adservices.topics.Topic;
import android.util.Pair;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.util.ObjectMapperProvider;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompMessage;

/**
 * This class is a wrapper around the Stomp Driver. It manages subscriptions
 * and dispatches messages by type
 */
public class StompManager {
    private static final String STOMP_GAME_ENDPOINT = "/topic/game/%s/messages";
    private static final String STOMP_USER_ENDPOINT = "/user/queue/messages";
    private static StompManager instance;
    private final StompDriver driver;
    private final ObjectMapper objectMapper;

    private PublishProcessor<MessageDto> dispatcher;
    private HashMap<String, Flowable<MessageDto>> originTopics;
    private CompositeDisposable originDisposables;
    private Disposable lifecycleDisposable;

    private StompManager(StompDriver stompDriver, ObjectMapper objectMapper){
        this.driver = stompDriver;
        this.objectMapper = objectMapper;
    }

    public Completable connect(String token){
        if(isConnected())
            shutdown();
        this.dispatcher = PublishProcessor.create();
        originTopics = new HashMap<>();
        originDisposables = new CompositeDisposable();
        return driver.connect(token).andThen(Completable.fromAction(() -> {
            lifecycleDisposable = lifecycle()
                    .filter(lifecycleEvent -> lifecycleEvent.getType() == LifecycleEvent.Type.CLOSED)
                    .subscribe(lifecycleEvent -> shutdown());
        }));
    }
    public void shutdown(){
        if(originDisposables != null)
            originDisposables.dispose();
        //signal complete to all Subscribers
        if(dispatcher != null)
            dispatcher.onComplete();
        if(lifecycleDisposable != null){
            lifecycleDisposable.dispose();
        }
        originTopics = null;
        dispatcher = null;
        driver.disconnect();
    }
    public boolean isConnected(){
        return driver.isConnected();
    }


    public <T> Flowable<T> filterByType(Class<T> messageType){
        return dispatcher.filter(messageType::isInstance)
                .map(messageType::cast);
    }

    public Flowable<LifecycleEvent> lifecycle(){
        return driver.lifecycle();
    }
    synchronized public static void initialize(@NotNull StompDriver driver, @NotNull ObjectMapper objectMapper) {
        instance = new StompManager(driver, objectMapper);
    }
    @NotNull
    public static StompManager getInstance(){
        return instance;
    }

    public void listenOnGame(String gameID){
        listenOn(String.format(STOMP_GAME_ENDPOINT, gameID));
    }
    public void listenPrivate(){
        listenOn(STOMP_USER_ENDPOINT);
    }

    public void listenOn(String topic){
        if(dispatcher == null)
            dispatcher = PublishProcessor.create();

        Flowable<MessageDto> originTopic = originTopics.get(topic);
        if(originTopic != null)
            return;

        originTopic = driver.getTopic(topic).map(message -> objectMapper.readValue(message.getPayload(), MessageDto.class));
        originTopics.put(topic, originTopic);
        originDisposables.add(originTopic.subscribe(dispatcher::onNext)); //TODO: Error handling for mapping exceptions if necessary
    }
}
