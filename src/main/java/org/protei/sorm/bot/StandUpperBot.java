package org.protei.sorm.bot;

import org.protei.sorm.bot.commands.CommandHandler;
import org.protei.sorm.bot.config.Props;
import org.protei.sorm.bot.persistance.ChatManager;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


@Component
@ComponentScan("org.protei.sorm.bot.standupper")
public class StandUpperBot extends TelegramLongPollingBot {
    private static final String CHAT_IDS_FILENAME = System.getProperty("user.home") + "/.standupper/chatIds.txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(StandUpperBot.class);

    private final Props props;
    private final CommandHandler handler;
    @Autowired
    public StandUpperBot(Props props, CommandHandler handler) {
        super();
        this.props = props;
        this.handler = handler;
        LOGGER.info("StandUpperBot created.");
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

    /////////////////////////////////////////////////////////////////////////////////

    private void initChatIdsFile() {

        File find = new File(CHAT_IDS_FILENAME);
        if (find.exists()) {
            try (Stream<String> stream = Files.lines(Paths.get(find.getPath()))) {
                stream.forEach(id -> chatIds.add(Long.parseLong(id)));
                chatIdsFile = find;
            } catch (IOException e) {
                LOGGER.warn("Can not read file with ids.", e);
            }
        } else {
            try {
                Path path = Paths.get(find.getPath());
                Files.createDirectories(path.getParent());
                Path filePath = Files.createFile(path);
                chatIdsFile = filePath.toFile();
            } catch (Exception ex) {
                LOGGER.error("Can not create file with chat ids", ex);
            }
        }
    }





    private void update() {
        forUser = false;

        config = Config.getConfig();
        if (standUp != null) {
            standUp.cancel();
        }
        timer.purge();


        standUp = new TimerTask() {
            @Override
            public void run() {
                if (dayIsOk() && forUser) {
                    sendNotificationForAll(config.getMessage());
                }
            }
        };

        timer.schedule(standUp, config.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

    }


    private void rewriteFile(Long chatId) {
        File find = new File(CHAT_IDS_FILENAME);
        if (find.exists()) {
            try (OutputStream out = new FileOutputStream(chatIdsFile)) {
                PrintStream printStream = new PrintStream(out);
                chatIds.forEach(printStream::println);
                printStream.println(chatId);
                LOGGER.info("Chat with id : " + chatId + " was added.");
            } catch (Exception ex) {
                LOGGER.error("Can not find file or write to file: " + chatIdsFile, ex);
            }
        } else {
            LOGGER.error("File with ids not exist.");
        }

    }



    synchronized private boolean dayIsOk() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (config != null) {
            return config.getDaysOfWeek().stream().anyMatch(s -> s.equals(day));
        } else {
            return false;
        }
    }
}
