package com.group2.catan_android.data.service;

import android.util.Pair;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.catan_android.data.live.MessageDto;
import com.group2.catan_android.data.util.ObjectMapperProvider;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import ua.naiksoftware.stomp.dto.StompMessage;

/**
 * This class is a wrapper around the Stomp Driver. It manages subscriptions
 * and provides an Interface to subscribe to StompEndpoints
 */
public class StompManager {
    private static StompManager instance;
    private final StompDriver driver;
    private final ObjectMapper objectMapper;

    private final Map<String, Map<Class<? super MessageDto>, PublishProcessor<? super MessageDto>>> typedProcessors = new HashMap<>();
    private final HashMap<String, Flowable<StompMessage>> rawFlowables = new HashMap<>();
    private final HashMap<String, Flowable<MessageDto>> messageFlowables = new HashMap<>();
    private StompManager(StompDriver stompDriver, ObjectMapper objectMapper){
        this.driver = stompDriver;
        this.objectMapper = objectMapper;
    }

    public void connect(String token){
        driver.connect(token);
    }

    synchronized public static void initialize(@NotNull StompDriver driver, @NotNull ObjectMapper objectMapper) {
        if (instance == null) {
            instance = new StompManager(driver, objectMapper);
        }
    }
    @NotNull
    public static StompManager getInstance(){
        return instance;
    }


    public Flowable<MessageDto> getMessageTopic(String topic){
        Flowable<MessageDto> messageDtoFlowable = messageFlowables.get(topic);
        if(messageDtoFlowable == null){
            messageFlowables.put(topic, getRawTopic(topic).map(
                    stompMessage -> objectMapper.readValue(stompMessage.getPayload(), MessageDto.class)));
        }
        return messageFlowables.get(topic);
    }

    public Flowable<StompMessage> getRawTopic(String topic){
        Flowable<StompMessage> rawFlowable = rawFlowables.get(topic);
        if(rawFlowable == null){
            rawFlowables.put(topic, driver.getTopic(topic));
        }
        return rawFlowables.get(topic);
    }
}
