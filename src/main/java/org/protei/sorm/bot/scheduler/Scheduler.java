package org.protei.sorm.bot.scheduler;

import org.protei.sorm.bot.StandUpperBot;
import org.protei.sorm.bot.config.Props;
import org.protei.sorm.bot.persistance.IChatManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private final IChatManager chatManager;
    private final StandUpperBot bot;
    private final Props props;


    @Autowired
    public Scheduler(IChatManager chatManager, StandUpperBot bot, Props props) {
        this.chatManager = chatManager;
        this.bot = bot;
        this.props = props;
    }

    @Scheduled(cron = "0 45 12 * * TUE-FRI")
    public void standUp() {
        chatManager.getReceivers()
                .forEach(c ->
                        bot.sendNotification(props.getMessage(), c)
                );
    }

    //TODO add trigger for update time
    private String cronConfig() {
        final char SPACE = ' ';
        StringBuilder cronBuilder = new StringBuilder();
        cronBuilder.append(props.getSeconds());
        cronBuilder.append(SPACE);
        cronBuilder.append(props.getMinutes());
        cronBuilder.append(SPACE);
        cronBuilder.append(props.getHours());
        cronBuilder.append(SPACE + '*' + SPACE + '*');
        String daysOfWeek = props.getDaysOfWeek();
        if (daysOfWeek != null && !daysOfWeek.isEmpty()){
            cronBuilder.append("TUE-FRI");
        }else {
            cronBuilder.append(daysOfWeek);
        }
        return cronBuilder.toString();
    }

}
