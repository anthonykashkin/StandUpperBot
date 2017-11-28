package org.protei.sorm.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    static {
        ApiContextInitializer.init();
    }

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
            try {
                new TelegramBotsApi().registerBot(applicationContext.getBean(StandUpperBot.class));
                LOGGER.info("StandUpperBot registered.");
            } catch (TelegramApiException e) {
                LOGGER.error("Can not register bot", e);
            }
            LOGGER.info("Context initialized.");
        } catch (Exception e) {
            LOGGER.error("Can not initialize context", e);
        }
    }
}