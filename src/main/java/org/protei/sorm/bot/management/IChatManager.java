package org.protei.sorm.bot.management;

import java.util.Set;

public interface IChatManager {
    boolean addChat(Long chatId);
    boolean removeChat(Long chatId);
    Set<Long> getReceivers();
}
