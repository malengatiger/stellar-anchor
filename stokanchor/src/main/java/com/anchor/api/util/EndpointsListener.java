package com.anchor.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


@Component
public class EndpointsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointsListener.class);
    int cnt = 0;
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 EndpointsListener: handleContextRefresh fired! " +
                "\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 Listing all available endpoints");
       cnt = 0;
        applicationContext.getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods().forEach((key, value) -> {
                    cnt++;
            LOGGER.info("\uD83C\uDF45 Anchor ENDPOINT #"+cnt+" \uD83C\uDF45 {} {}", key, value);
        });
        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 EndpointsListener: Total EndPoints: " + cnt);
    }
}
