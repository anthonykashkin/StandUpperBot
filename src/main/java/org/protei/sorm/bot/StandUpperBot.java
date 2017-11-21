package org.protei.sorm.bot;

import org.protei.sorm.bot.commands.Commands;
import org.protei.sorm.bot.configuration.ConfigurationDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class StandUpperBot extends TelegramLongPollingBot {
    final static ConcurrentLinkedDeque<Long> chatIds = new ConcurrentLinkedDeque<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(StandUpperBot.class);

    private static ConfigurationDateTime config;

    private File chatIdsFile;

    private TimerTask standUp = new TimerTask() {
        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            if (dayIsOk()) {
                if (calendar.get(Calendar.HOUR) == config.getHours() &&
                        calendar.get(Calendar.MINUTE) == config.getMinutes() &&
                        calendar.get(Calendar.SECOND) == config.getSeconds()) {
                    sendNotification("Stand Up");
                }
            }
        }
    };

    private static Timer timer = new Timer();

    public StandUpperBot() {
        super();
        initChatIdsFile();
        config = ConfigurationDateTime.getConfig();
        timer.schedule(standUp, 1000);
    }

    private void initChatIdsFile() {
        File find = new File("./chatIds.txt");
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
                chatIdsFile = Files.createFile(filePath).toFile();
            } catch (Exception ex) {
                LOGGER.error("Can not create file with chat ids", ex);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "org.protei.sorm.bot.StandUpperBot";
    }

    @Override
    public String getBotToken() {
        /*
        try (InputStream inputStream = new FileInputStream("org.protei.sorm.bot/token.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("token");
        } catch (IOException io) {
            LOGGER.error( "Can not get token. ", io);
            return "";
        }*/
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (Objects.equals(update.getMessage().getText(), Commands.cancelCommand)) {
            removeChat(update.getMessage().getChatId());
            LOGGER.info( "Received command CANCEL from: " + update.getMessage().getFrom());
        } else if ((Objects.equals(update.getMessage().getText(), Commands.startCommand))) {
            addChat(update.getMessage().getChatId());
            LOGGER.info( "Received command START from: " + update.getMessage().getFrom());
        } else if ((Objects.equals(update.getMessage().getText(), Commands.update))) {
            ConfigurationDateTime tmp = config;
            try {
                config = ConfigurationDateTime.getConfig();
                LOGGER.info( "Received command UPDATE from: " + update.getMessage().getFrom());
            } catch (Exception ex) {
                config = tmp;
                LOGGER.error( "On update config. ", ex);
            }
        } else if (update.getMessage() != null && update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {
            Long chatId = update.getMessage().getChatId();
            addChat(chatId);
            LOGGER.info( "Try to add chat with id: " + chatId);
        } else {
            LOGGER.info( "Something is happened: " + update);
        }
    }

    private void removeChat(Long chatId) {
        chatIds.remove(chatId);

        File find = new File("./chatIds.txt");
        if (find.exists()) {
            try (Stream<String> stream = Files.lines(Paths.get(find.getPath()))) {
                stream.forEach(id -> chatIds.add(Long.parseLong(id)));
                chatIdsFile = find;
            } catch (IOException e) {
                LOGGER.warn("Can not read file with ids.", e);
            }
        } else {
            LOGGER.warn( "Can not find file with ids.");
        }
    }

    public void addChat(Long chatId) {
        chatIds.add(chatId);

        try (OutputStream out = new FileOutputStream(chatIdsFile)) {
            PrintStream printStream = new PrintStream(out);
            printStream.println(chatId);
            LOGGER.info("Chat with id : " + chatId + " was added.");
        } catch (Exception ex) {
            LOGGER.error( "Can not find file or write to file: " + chatIdsFile, ex);
        }

    }

    public void sendNotification(String text) {
        chatIds.forEach(s -> {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(s);
            sendMessage.setText(text);
            try {
                sendMessage(sendMessage);
                LOGGER.info( "Message with text: \"" + text + "\" was send to chat with id: " + s);
            } catch (TelegramApiException e) {
                LOGGER.error( "Can not send message", e);
            }
        });
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
