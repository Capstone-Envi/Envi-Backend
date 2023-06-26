package com.capstone.project.domain.iot.mqtt;

public abstract class MqttConfig {
    protected final int qos = 2;
    protected Boolean hasSSL = false; /* By default SSL is disabled */
    protected int port = 1883; /* Default port */
    protected final String userName = "envisystem65";
    protected final String password = "envi123";
    protected final String TCP = "tcp://";
    protected final String SSL = "ssl://";

    /**
     * Custom Configuration
     *
     * @param broker
     * @param port
     * @param ssl
     * @param withUserNamePass
     */
    protected abstract void config(String broker, Integer port, Boolean ssl, Boolean withUserNamePass);

    /**
     * Default Configuration
     */
    protected abstract void config();
}