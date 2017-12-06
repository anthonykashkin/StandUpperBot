package org.protei.sorm.bot;

import org.protei.sorm.bot.commands.ICommandHandler;
import org.protei.sorm.bot.config.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;


@Component
@ComponentScan("org.protei.sorm.bot.standupper")
public class StandUpperBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandUpperBot.class);

    private final Props props;

    @Autowired
    private ICommandHandler handler;

    @Autowired
    public StandUpperBot(Props props) {
        super();
        this.props = props;
        LOGGER.info("StandUpperBot created.");
    }

    @PostConstruct
    public void init() {
           handler.setStandUpperBot(this);
    }

    @Override
    public String getBotUsername() {
        return "org.protei.sorm.bot.StandUpperBot";
    }

    @Override
    public String getBotToken() {
        return props.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null) {
            handler.handle(message);
        }
    }

    public void sendNotification(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
            LOGGER.info("Message with text: \"" + text + "\" was send to chat with id: " + chatId);
        } catch (TelegramApiException e) {
            LOGGER.error("Can not send message", e);
        }
    }

}
