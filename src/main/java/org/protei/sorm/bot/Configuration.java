package org.protei.sorm.bot;

import org.telegram.telegrambots.logging.BotLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Configuration {
    private static final String LOGTAG = "org.protei.sorm.bot.Configuration";
    private static final String propFilePath = System.getProperty("user.home") + "/.standupper/timing.xml";
    private final static Properties properties = new Properties();
    private int hours;
    private int minutes;
    private int seconds;
    private List<Integer> daysOfWeek;

    private Configuration(int hours, int minutes, int seconds, List<Integer> daysOfWeek) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.daysOfWeek = daysOfWeek;
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
                BotLogger.error(LOGTAG, e);
            }

            Integer hours = Integer.valueOf(properties.getProperty("hours"));
            Integer minutes = Integer.valueOf(properties.getProperty("minutes"));
            Integer seconds = Integer.valueOf(properties.getProperty("seconds"));
            ArrayList<Integer> daysOfWeek = new ArrayList<>();
            for (String s : properties.getProperty("daysOfWeak").split("\\,")) {
                daysOfWeek.add(Integer.valueOf(s));
            }
            return new Configuration(hours, minutes, seconds, daysOfWeek);
        } catch (Exception e) {
            BotLogger.severe(LOGTAG, e);
            return null;
        }
    }

    private static void writeDefaultConfig(File file) throws IOException {
        String config = new String("hours=12\n" +
                "minutes=45\n" +
                "seconds=0\n" +
                "daysOfWeak=2,3,4,5");
        Path path = Paths.get(propFilePath);
        Files.createDirectories(path.getParent());
        Files.createFile(path);

        try (OutputStream out = new FileOutputStream(file)) {
            PrintStream printStream = new PrintStream(out);
            printStream.print(config);
        }
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

    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }
}
