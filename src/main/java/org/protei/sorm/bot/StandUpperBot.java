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
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


public class StandUpperBot extends TelegramLongPollingBot {
    public static final String CHAT_IDS_FILENAME = System.getProperty("user.home") + "/.standupper/chatIds.txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(StandUpperBot.class);
    final static ConcurrentSkipListSet<Long> chatIds = new ConcurrentSkipListSet<>();

    private static ConfigurationDateTime config;

    private File chatIdsFile;
    private TimerTask standUp;

    private Timer timer = new Timer();

    volatile private Boolean forUser;

    public StandUpperBot() {
        super();
        initChatIdsFile();
        config = ConfigurationDateTime.getConfig();
        reconfig();
        forUser = true;
    }

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

    @Override
    public String getBotUsername() {
        return "org.protei.sorm.bot.StandUpperBot";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (Objects.equals(update.getMessage().getText(), Commands.cancelCommand)) {
            removeChat(update.getMessage().getChatId());
            LOGGER.info("Received command CANCEL from: " + update.getMessage().getFrom());
            sendNotification("Ваш чат удален из рассылки", update.getMessage().getChatId());
        } else if ((Objects.equals(update.getMessage().getText(), Commands.startCommand))) {
            addChat(update.getMessage().getChatId());
            reconfig();
            LOGGER.info("Received command START from: " + update.getMessage().getFrom());
            sendNotification("Ваш чат добавлен в рассылку", update.getMessage().getChatId());
            forUser = true;
        } else if ((Objects.equals(update.getMessage().getText(), Commands.update))) {
            ConfigurationDateTime tmp = config;
            try {
                reconfig();
                sendNotificationForAll("Время обновлено. Теперь уведомление придет в : "
                        + config.getTime().getHours() + ':'
                        + config.getTime().getMinutes()
                        + ':' + config.getTime().getSeconds() + '\n' +
                        "Текущее время : " + Calendar.getInstance().getTime()
                );
                LOGGER.info("Received command UPDATE from: " + update.getMessage().getFrom());
                forUser = true;
            } catch (Exception ex) {
                config = tmp;
                LOGGER.error("On reconfig config. ", ex);
            }
        } else if (update.getMessage() != null && update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {
            Long chatId = update.getMessage().getChatId();
            addChat(chatId);
            LOGGER.info("Try to add chat with id: " + chatId);
        } else {
            LOGGER.info("Something is happened: " + update);
        }
    }

    private void reconfig() {
        forUser = false;

        config = ConfigurationDateTime.getConfig();
        if (standUp != null) {
            standUp.cancel();
        }
        timer.purge();


        standUp = new TimerTask() {
            @Override
            public void run() {
                if (dayIsOk() && forUser) {
                    sendNotificationForAll("Stand Up");
                }
            }
        };
        timer.schedule(standUp, config.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

    }

    private synchronized void removeChat(Long chatId) {
        if (chatIds.remove(chatId)) {
            rewriteFile(chatId);
        }
    }

    private synchronized void addChat(Long chatId) {
        if (chatIds.add(chatId)) {
            rewriteFile(chatId);
        }
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

    private void sendNotificationForAll(String text) {
        chatIds.forEach(s -> sendNotification(text, s));
    }

    private void sendNotification(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
            LOGGER.info("Message with text: \"" + text + "\" was send to chat with id: " + chatId);
        } catch (TelegramApiException e) {
            LOGGER.error("Can not send message", e);
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
