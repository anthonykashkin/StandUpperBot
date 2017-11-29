package org.protei.sorm.bot.persistance;

import java.util.Set;

public interface IChatManager {
    boolean addChat(Long chatId);
    boolean removeChat(Long chatId);
    Set<Long> getReceivers();
}
