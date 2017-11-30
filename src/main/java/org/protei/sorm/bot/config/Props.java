package org.protei.sorm.bot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Props {
    private static final Logger logger = LoggerFactory.getLogger(Props.class);

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
    private String pathToChats;

    public String getPathToChats() {
        return pathToChats;
    }

    public void setPathToChats(String pathToChats) {
        this.pathToChats = pathToChats;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
