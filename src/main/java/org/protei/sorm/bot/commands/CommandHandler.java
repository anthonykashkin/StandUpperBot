package org.protei.sorm.bot.commands;

import org.protei.sorm.bot.StandUpperBot;
import org.protei.sorm.bot.config.Props;
import org.protei.sorm.bot.persistance.ChatManager;
import org.protei.sorm.bot.persistance.IChatManager;
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

    private final StandUpperBot standUpperBot;
    private final IChatManager chatManager;
    private final Props props;

    @Autowired
    public CommandHandler(StandUpperBot standUpperBot, IChatManager chatManager, Props props) {
        this.standUpperBot = standUpperBot;
        this.chatManager = chatManager;
        this.props = props;
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
            standUpperBot.sendNotification("Ваш чат удален из рассылки", chatId);
        }
    }

    private void start(Long chatId) {
        chatManager.addChat(chatId);
        //todo standUpperBot.update();
        standUpperBot.sendNotification("Ваш чат добавлен в рассылку", chatId);
    }

    private void update() {
        //todo standUpperBot.update();
        chatManager.getReceivers()
                .forEach(c -> standUpperBot.sendNotification("Время обновлено. Теперь уведомление придет в "
                        + props.getHours() + ':'
                        + props.getMinutes() + '\n' +
                        "Текущее время : " + Calendar.getInstance().getTime(), c)
                );
    }
}