package org.protei.sorm.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Props {

    @Value("${hours}")
    private int hours;

    @Value("${minutes}")
    private int minutes;

    @Value("${seconds}")
    private int seconds;

    @Value("${daysOfWeek}")
    private String daysOfWeek;

    @Value("${message}")
    private String message;

    @Value("${token}")
    private String token;

    @Value("${pathToChatIds}")
    private String pathToChatIds;

    public String getPathToChats() {
        return pathToChatIds;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
