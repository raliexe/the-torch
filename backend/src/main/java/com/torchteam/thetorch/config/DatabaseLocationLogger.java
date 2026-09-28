package com.torchteam.thetorch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DatabaseLocationLogger {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void logDatabaseLocation() {
        log.info("SQLite database URL: {}", datasourceUrl);
        log.info("Backend working directory: {}", System.getProperty("user.dir"));
    }
}
