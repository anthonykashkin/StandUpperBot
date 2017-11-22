package org.protei.sorm.bot.configuration;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Configuration {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
    private static final String propFilePath = System.getProperty("user.home") + "/.standupper/timing.properties";
    private final static Properties properties = new Properties();
    private int hours;
    private int minutes;
    private int seconds;
    private List<Integer> daysOfWeek;
    private String message;
    private String token;

    private Configuration(int hours, int minutes, int seconds, List<Integer> daysOfWeek, String message, String token) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.daysOfWeek = daysOfWeek;
        this.message = message;
        this.token = token;
    }

    public static Configuration getConfig() {
        try {
            File file = new File(propFilePath);

            if (!file.exists()) {
                writeDefaultConfig(file);
            }

            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
            } catch (IOException e) {
                LOGGER.error( "Can not load config file. ", e);
            }

            Integer hours = Integer.valueOf(properties.getProperty("hours"));
            Integer minutes = Integer.valueOf(properties.getProperty("minutes"));
            Integer seconds = Integer.valueOf(properties.getProperty("seconds"));
            ArrayList<Integer> daysOfWeek = new ArrayList<>();
            for (String s : properties.getProperty("daysOfWeek").split("\\,")) {
                daysOfWeek.add(Integer.valueOf(s));
            }
            String message = properties.getProperty("message");
            String token = properties.getProperty("token");
            return new Configuration(hours, minutes, seconds, daysOfWeek, message, token);
        } catch (Exception e) {
            LOGGER.error( "Can not read config file. ", e);
            return null;
        }
    }

    private static void writeDefaultConfig(File file) throws IOException {
        String config = "hours=12\n" +
                "minutes=44\n" +
                "seconds=0\n" +
                "daysOfWeek=2,3,4,5\n" +
                "message=StandUp\n" +
                "token=<TOKEN>";

        Path path = Paths.get(propFilePath);
        Files.createDirectories(path.getParent());
        Files.createFile(path);

        try (OutputStream out = new FileOutputStream(file)) {
            PrintStream printStream = new PrintStream(out);
            printStream.print(config);
        }
    }

    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    public Date getTime() {
        Calendar dateTime = Calendar.getInstance();
        dateTime.set(Calendar.HOUR_OF_DAY, hours);
        dateTime.set(Calendar.MINUTE, minutes);
        dateTime.set(Calendar.SECOND, seconds);
        return dateTime.getTime();
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
