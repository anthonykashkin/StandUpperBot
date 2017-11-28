package org.protei.sorm.bot.commands;

import org.telegram.telegrambots.api.objects.Message;

public interface ICommandHandler {
    void handle(Message message);
}
