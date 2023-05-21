package com.capstone.project.config;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.p6spy.engine.spy.P6SpyOptions;

@Configuration
public class SQLTraceConfiguration {
    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(SQLTracer.class.getName());
    }
}