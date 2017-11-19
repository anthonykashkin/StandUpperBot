package org.protei.sorm.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String LOGTAG = "MAIN";
    private static final Logger LOGGER = Logger.getLogger(LOGTAG);

    public static void main(String[] args) {
        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                telegramBotsApi.registerBot(new StandUpperBot());
            } catch (TelegramApiException e) {
                LOGGER.log(Level.ALL, "Can not register bot", e);
            }
        } catch (Exception e) {
            LOGGER.log(Level.ALL, "Can not initialize context", e);
        }
    }
}