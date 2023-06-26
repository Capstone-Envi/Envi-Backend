package com.capstone.project.domain.iot.mqtt;

public interface MQTTPublisherBase {

    /**
     * Publish message
     *
     * @param topic
     * @param message
     */
    public void publishMessage(String topic, String message);

    /**
     * Disconnect MQTT Client
     */
    public void disconnect();

}