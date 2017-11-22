package org.protei.sorm.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                telegramBotsApi.registerBot(new StandUpperBot());
                LOGGER.info("StandUpperBot registered.");
            } catch (TelegramApiException e) {
                LOGGER.error( "Can not register bot", e);
            }
            LOGGER.info("Context initialized.");
        } catch (Exception e) {
            LOGGER.error( "Can not initialize context", e);
        }
    }
}