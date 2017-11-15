package org.protei.sorm.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class StandUpperBot extends TelegramLongPollingBot {
    private static final String LOGTAG = "org.protei.sorm.bot.StandUpperBot";

    final static Set<Long> chatIds = new HashSet<>();

    public StandUpperBot() {
        super();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 12);
        today.set(Calendar.MINUTE, 45);
        today.set(Calendar.SECOND, 0);

        TimerTask standUp = new TimerTask() {
            @Override
            public void run() {
                if (dayIsOk()) {
                    sendMsg("Stand Up");
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(standUp, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
    }

    @Override
    public String getBotUsername() {
        return "org.protei.sorm.bot.StandUpperBot";
    }

    @Override
    public String getBotToken() {
        return "TOKEN";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (Objects.equals(update.getMessage().getText(), "stop")) {
            chatIds.remove(update.getMessage().getChatId());
            BotLogger.info(LOGTAG, update.toString());
        } else if ((Objects.equals(update.getMessage().getText(), "start"))) {
            chatIds.add(update.getMessage().getChatId());
            BotLogger.info(LOGTAG, update.toString());
        } else if (update.getMessage() != null && update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {
            chatIds.add(update.getMessage().getChatId());
        } else {
            BotLogger.info(LOGTAG, update.toString());
        }

        chatIds.forEach(chatId ->
                BotLogger.info(LOGTAG, chatId.toString()));
    }

    public void sendMsg(String text) {
        chatIds.forEach(s -> {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(s);
            sendMessage.setText(text);
            try {
                sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        });
    }

    private boolean dayIsOk() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (day == 2 || day == 3 || day == 4 || day == 5) {
            return true;
        } else {
            return false;
        }
    }
}
