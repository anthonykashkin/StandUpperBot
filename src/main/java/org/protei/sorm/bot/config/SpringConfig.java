package org.protei.sorm.bot.config;

import org.protei.sorm.bot.StandUpperBot;
import org.protei.sorm.bot.commands.CommandHandler;
import org.protei.sorm.bot.commands.ICommandHandler;
import org.protei.sorm.bot.management.ChatManager;
import org.protei.sorm.bot.management.IChatManager;
import org.protei.sorm.bot.scheduler.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@ComponentScan(basePackages = "org.protei.sorm.bot")
public class SpringConfig {

    private final Props props;
    private final IChatManager chatManager;
    private final ICommandHandler handler;
    private final StandUpperBot bot;

    @Autowired
    public SpringConfig(Props props, IChatManager chatManager, ICommandHandler handler, StandUpperBot bot) {
        this.props = props;
        this.chatManager = chatManager;
        this.handler = handler;
        this.bot = bot;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
        properties.setLocation(new FileSystemResource("env/standupper.properties"));
        properties.setIgnoreResourceNotFound(false);
        return properties;
    }

    @Bean
    public ChatManager chatManager() {
        return new ChatManager(props);
    }

    @Bean
    public ICommandHandler commandHandler() {
        return new CommandHandler(bot, chatManager);
    }

    @Bean
    public StandUpperBot standUpperBot() {
        return new StandUpperBot(props, handler);
    }

    @Bean
    public Scheduler scheduler() {
        return new Scheduler(chatManager, bot, props);
    }
}
