package org.protei.sorm.bot.persistance;

public interface IChatManager {
    void init();
    boolean addChat(Long chatId);
    boolean removeChat(Long chatId);
}
