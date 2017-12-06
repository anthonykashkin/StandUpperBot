package org.protei.sorm.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Props {
    @Value("${message}")
    private String message;

    @Value("${token}")
    private String token;

    @Value("${pathToChatIds}")
    private String pathToChatIds;

    public String getPathToChats() {
        return pathToChatIds;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
