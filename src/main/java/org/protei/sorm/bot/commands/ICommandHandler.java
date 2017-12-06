package org.protei.sorm.bot.commands;

import org.protei.sorm.bot.StandUpperBot;
import org.telegram.telegrambots.api.objects.Message;

public interface ICommandHandler {

    String startCommand = "/standup_start";

    String cancelCommand = "/standup_cancel";

    @Deprecated
    String update = "/standup_update";

    void handle(Message message);

    void setStandUpperBot(StandUpperBot standUpperBot);
}
