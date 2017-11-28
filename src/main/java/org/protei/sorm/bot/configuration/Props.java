package org.protei.sorm.bot.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Props {

    private static final Logger logger = LoggerFactory.getLogger(Props.class);

    @Value("$hours")
    private Integer hours;
    @Value("$minutes")
    private Integer minutes;
    @Value("$seconds")
    private Integer seconds;
    @Value("$daysOfWeek")
    private Integer[] daysOfWeek;
    @Value("$message")
    private String message;
    @Value("$token")
    private String token;

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public Integer[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(Integer[] daysOfWeek) {
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
