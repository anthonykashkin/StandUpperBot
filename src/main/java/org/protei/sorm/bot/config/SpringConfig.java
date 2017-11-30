package org.protei.sorm.bot.config;

import org.protei.sorm.bot.StandUpperBot;
import org.protei.sorm.bot.commands.ICommandHandler;
import org.protei.sorm.bot.persistance.ChatManager;
import org.protei.sorm.bot.persistance.IChatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@ComponentScan(basePackages = "org.protei.sorm.bot")
public class SpringConfig {

    private final Props props;
    private final ICommandHandler handler;
    private final IChatManager chatManager;

    @Autowired
    public SpringConfig(Props props, ICommandHandler handler, IChatManager chatManager) {
        this.props = props;
        this.handler = handler;
        this.chatManager = chatManager;
    }

    @Bean
    public Props props(){
        Props props = new Props();
        //todo read props from property
        return props;
    }

    @Bean
    ChatManager chatManager() {
        return new ChatManager(props());
    }

    @Bean
    public StandUpperBot standUpperBot() {
        return new StandUpperBot(props, handler);
    }

}
