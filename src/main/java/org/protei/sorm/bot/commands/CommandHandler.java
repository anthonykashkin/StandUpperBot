package org.protei.sorm.bot.commands;

import org.protei.sorm.bot.StandUpperBot;
import org.protei.sorm.bot.persistance.ChatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Calendar;
import java.util.Objects;

@Component
public class CommandHandler implements ICommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    @Autowired
    private StandUpperBot standUpperBot;

    @Autowired
    private ChatManager chatManager;

    public void handle(Message message) {
        if (message != null) {
            if (Objects.equals(message.getText(), Commands.cancelCommand)) {

                LOGGER.info("Received command CANCEL from: " + message.getFrom());

                if (chatManager.removeChat(message.getChatId())) {
                    standUpperBot.sendNotification("Ваш чат удален из рассылки", message.getChatId());
                }

            } else if ((Objects.equals(message.getText(), Commands.startCommand))) {

                chatManager.addChat(message.getChatId());
                standUpperBot.update();
                LOGGER.info("Received command START from: " + message.getFrom());
                standUpperBot.sendNotification("Ваш чат добавлен в рассылку", message.getChatId());

            } else if ((Objects.equals(message.getText(), Commands.update))) {
                try {
                    standUpperBot.update();
                    standUpperBot.sendNotificationForAll("Время обновлено. Теперь уведомление придет в "
                            + config.getTime().getHours() + ':'
                            + config.getTime().getMinutes() + '\n' +
                            "Текущее время : " + Calendar.getInstance().getTime()
                    );
                    LOGGER.info("Received command UPDATE from: " + message.getFrom());
                } catch (Exception ex) {
                    LOGGER.error("On update config. ", ex);
                }
            }
        }
    }
}
