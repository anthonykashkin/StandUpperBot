package org.protei.sorm.bot.configuration;

import org.protei.sorm.bot.StandUpperBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.protei.sorm.bot")
public class SpringConfig {
    private static final Logger logger = LoggerFactory.getLogger(SpringConfig.class);

    private final Props props;

    @Autowired
    public SpringConfig(Props props) {
        this.props = props;
    }

    @Bean
    public StandUpperBot standUpperBot() {
        return new StandUpperBot(props);
    }
}
