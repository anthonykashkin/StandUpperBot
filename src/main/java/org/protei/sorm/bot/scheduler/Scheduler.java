package org.protei.sorm.bot.scheduler;

import org.protei.sorm.bot.persistance.IChatManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private final IChatManager chatManager;

    @Autowired

    public Scheduler(IChatManager chatManager) {
        this.chatManager = chatManager;
    }
}
