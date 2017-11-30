package org.protei.sorm.bot;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.protei.sorm.bot.commands.CommandHandler;
import org.protei.sorm.bot.commands.ICommandHandler;
import org.protei.sorm.bot.config.Props;
import org.protei.sorm.bot.config.SpringConfig;
import org.protei.sorm.bot.management.ChatManager;
import org.protei.sorm.bot.management.IChatManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.mock;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
@Configuration
public class SpringInitTest {

    @Test
    public void test() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        applicationContext.getBean("props");
    }

}
