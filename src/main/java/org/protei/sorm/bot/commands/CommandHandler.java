package org.protei.sorm.bot.commands;

import org.protei.sorm.bot.StandUpperBot;
import org.protei.sorm.bot.management.IChatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Objects;

@Component
public class CommandHandler implements ICommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    private final StandUpperBot standUpperBot;
    private final IChatManager chatManager;

    @Autowired
    public CommandHandler(StandUpperBot standUpperBot, IChatManager chatManager) {
        this.standUpperBot = standUpperBot;
        this.chatManager = chatManager;
    }


    @Override
    public void handle(Message message) {
        if (Objects.equals(message.getText(), ICommandHandler.cancelCommand)) {
            LOGGER.info("Received command CANCEL from: " + message.getFrom());
            cancel(message.getChatId());
        } else if ((Objects.equals(message.getText(), ICommandHandler.startCommand))) {
            LOGGER.info("Received command START from: " + message.getFrom());
            start(message.getChatId());
        } else if ((Objects.equals(message.getText(), ICommandHandler.update))) {
            LOGGER.info("Received command UPDATE from: " + message.getFrom());
            update();
        }
    }


    private void cancel(Long chatId) {
        if (chatManager.removeChat(chatId)) {
            standUpperBot.sendNotification("Ваш чат удален из рассылки.", chatId);
        }
    }

    private void start(Long chatId) {
        boolean isExist = chatManager.addChat(chatId);
        if (isExist) {
            standUpperBot.sendNotification("Ваш чат уже добавлен. ", chatId);
        } else {
            //todo standUpperBot.update();
            standUpperBot.sendNotification("Ваш чат добавлен в рассылку.", chatId);
        }
    }

    @Deprecated
    private void update() {
        chatManager.getReceivers()
                .forEach(c -> standUpperBot.sendNotification("Команда UPDATE пока не поддерживается.", c));
        /* todo standUpperBot.update();
        chatManager.getReceivers()
                .forEach(c -> standUpperBot.sendNotification("Время обновлено. Теперь уведомление придет в "
                        + props.getHours() + ':'
                        + props.getMinutes() + '\n' +
                        "Текущее время : " + Calendar.getInstance().getTime(), c)
                );
                */
    }
}
