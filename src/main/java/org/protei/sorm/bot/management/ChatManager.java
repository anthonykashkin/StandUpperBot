package org.protei.sorm.bot.management;

import org.protei.sorm.bot.config.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

@Component
public class ChatManager implements IChatManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static ConcurrentSkipListSet<Long> chatIds = new ConcurrentSkipListSet<>();

    private Path pathToFile;

    @Autowired
    public ChatManager(Props props) {
        this.pathToFile = Paths.get(props.getPathToChats());
        init();
    }

    private void init() {
        logger.debug("Init...");

        if (pathToFile == null) {
            pathToFile = Paths.get(System.getProperty("user.home") + "/.standupper/chatIds.txt");
        }

        if (!Files.exists(pathToFile)) {
            try {
                Files.createDirectories(pathToFile.getParent());
                Files.createFile(pathToFile);
                logger.debug("File was created.");
            } catch (IOException e) {
                logger.error("Initialization failed. ", e);
            }
        } else {
            try (Stream<String> lines = Files.lines(pathToFile)) {
                lines.filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .forEach(chatIds::add);
            } catch (IOException e) {
                logger.error("File read error. ", e);
            }
        }

        logger.debug("Init completed.");
    }

    @Override
    public boolean addChat(Long chatId) {
        boolean add = chatIds.add(chatId);
        if (!add) {
            return false;
        }
        if (Files.exists(pathToFile)) {
            try {
                Files.write(pathToFile, ("\n" + String.valueOf(chatId)).getBytes(), StandardOpenOption.APPEND);
            } catch (IOException io) {
                logger.error("Can`t write chat to file. Recommend do it manually.", io);
            }
        }
        return true;
    }

    @Override
    public boolean removeChat(Long chatId) {
        if (Files.exists(pathToFile)) {
            try {
                String content = new String(Files.readAllBytes(pathToFile));
                content = content.replaceAll("\n" + String.valueOf(chatId), "");
                Files.write(pathToFile, content.getBytes());
            } catch (IOException io) {
                logger.warn("Can`t remove chat from file. Recommend do it manually.", io);
            }
        } else {
            logger.warn("File doesn`t exist.");
        }

        return chatIds.remove(chatId);
    }

    @Override
    public Set<Long> getReceivers() {
        return Collections.unmodifiableSet(chatIds);
    }
}
