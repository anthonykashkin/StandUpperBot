package org.protei.sorm.bot;

import org.protei.sorm.bot.commands.Commands;
import org.protei.sorm.bot.configuration.ConfigurationDateTime;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class StandUpperBot extends TelegramLongPollingBot {
    final static ConcurrentLinkedDeque<Long> chatIds = new ConcurrentLinkedDeque<>();
    private static final String LOGTAG = "org.protei.sorm.bot.StandUpperBot";
    private static final Logger LOGGER = Logger.getLogger(LOGTAG);
    private ConfigurationDateTime config;

    private File chatIdsFile;

    private TimerTask standUp = new TimerTask() {
        @Override
        public void run() {
            if (dayIsOk()) {
                sendMsg("Stand Up");
            }
        }
    };

    private Timer timer = new Timer();

    public StandUpperBot() {
        super();
        initChatIdsFile();
        updateConfig();
    }

    private void initChatIdsFile() {
        File find = new File("./chatIds.txt");
        if (find.exists()) {
            try (Stream<String> stream = Files.lines(Paths.get(find.getPath()))) {
                stream.forEach(id -> chatIds.add(Long.parseLong(id)));
                chatIdsFile = find;
            } catch (IOException e) {
                LOGGER.log(Level.CONFIG, "Can not read file with ids.", e);
            }
        } else {
            try {
                Path path = Paths.get(find.getPath());
                Files.createDirectories(path.getParent());
                Path filePath = Files.createFile(path);
                chatIdsFile = Files.createFile(filePath).toFile();
            } catch (Exception ex) {
                LOGGER.log(Level.ALL, "Can not create file with chat ids");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "org.protei.sorm.bot.StandUpperBot";
    }

    @Override
    public String getBotToken() {
        try (InputStream inputStream = new FileInputStream("token.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("token");
        } catch (IOException io) {
            LOGGER.log(Level.ALL, "Can not get token. ", io);
            return "";
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (Objects.equals(update.getMessage().getText(), Commands.cancelCommand)) {
            chatIds.remove(update.getMessage().getChatId());
            LOGGER.log(Level.INFO, "Received command CANCEL from: " + update.getMessage().getContact());
        } else if ((Objects.equals(update.getMessage().getText(), Commands.startCommand))) {
            chatIds.add(update.getMessage().getChatId());
            LOGGER.log(Level.INFO, "Received command START from: " + update.getMessage().getContact());
        } else if ((Objects.equals(update.getMessage().getText(), Commands.update))) {
            ConfigurationDateTime tmp = config;
            try {
                updateConfig();
            } catch (Exception ex) {
                config = tmp;
                LOGGER.log(Level.SEVERE, "Error hapend", ex);
            }
            LOGGER.log(Level.INFO, "Received command UPDATE from: " + update.getMessage().getContact());
        } else if (update.getMessage() != null && update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {
            Long chatId = update.getMessage().getChatId();
            addChat(chatId);
            LOGGER.log(Level.INFO, "Try to add chat with id: " + chatId);
        } else {
            LOGGER.log(Level.INFO, "Something is happened: " + update);
        }

        chatIds.forEach(chatId ->
                BotLogger.info(LOGTAG, chatId.toString()));
    }

    private void updateConfig() {
        config = ConfigurationDateTime.getConfig();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, config.getHours());
        today.set(Calendar.MINUTE, config.getMinutes());
        today.set(Calendar.SECOND, config.getSeconds());

        timer.cancel();
        timer.schedule(standUp, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
    }

    public void addChat(Long chatId) {
        chatIds.add(chatId);

        try (OutputStream out = new FileOutputStream(chatIdsFile)) {
            PrintStream printStream = new PrintStream(out);
            printStream.println(chatId);
            LOGGER.log(Level.INFO, "Chat with id : " + chatId + " was added.");
        } catch (Exception ex) {
            LOGGER.log(Level.ALL, "Can not find file or write to file: " + chatIdsFile, ex);
        }

    }

    public void sendMsg(String text) {
        chatIds.forEach(s -> {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(s);
            sendMessage.setText(text);
            try {
                sendMessage(sendMessage);
                LOGGER.log(Level.ALL, "Message with text: \"" + text + "\" was send to chat with id: " + s);
            } catch (TelegramApiException e) {
                LOGGER.log(Level.ALL, "Can not send message", e);
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
