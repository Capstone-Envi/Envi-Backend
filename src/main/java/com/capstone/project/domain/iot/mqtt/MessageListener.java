package com.capstone.project.domain.iot.mqtt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListener implements Runnable {
    private final MQTTSubscriberBase subscriber;

    @Override
    public void run() {
        subscriber.subscribeMessage("test");
    }

}