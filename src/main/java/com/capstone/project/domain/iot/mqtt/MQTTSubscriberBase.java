package com.capstone.project.domain.iot.mqtt;

public interface MQTTSubscriberBase {
    /**
     * Subscribe message
     *
     * @param topic
     */
    public void subscribeMessage(String topic);

    /**
     * Disconnect MQTT Client
     */
    public void disconnect();
}