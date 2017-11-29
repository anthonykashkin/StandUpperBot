package org.protei.sorm.bot.persistance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class ChatManager implements IChatManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static ConcurrentSkipListSet<Long> chatIds = new ConcurrentSkipListSet<>();

    private Path pathToFile;

    @Autowired(required = false)
    public ChatManager(Path pathToFile) {
        this.pathToFile = pathToFile;
        init();
    }

    public void init() {
        logger.debug("Init...");
        if (!Files.exists(pathToFile)) {
            try {
                Files.createFile(pathToFile);
            } catch (IOException e) {
                logger.error("Initialization failed. ", e);
            }
        }
    }

    @Override
    public boolean addChat(Long chatId) {

        return chatIds.add(chatId);
        //todo write to file
    }

    @Override
    public boolean removeChat(Long chatId) {
        return chatIds.remove(chatId);
        //todo remove from file
    }

    @Override
    public Set<Long> getReceivers() {
        return Collections.unmodifiableSet(chatIds);
    }
}
