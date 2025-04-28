package com.github.jahong1r_t.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BotConfig {
    public String BOT_TOKEN;
    public String BOT_USERNAME;
    public Long BOT_ADMIN = 5699941692L;

    public BotConfig() {
        try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            this.BOT_TOKEN = properties.getProperty("bot.token");
            this.BOT_USERNAME = properties.getProperty("bot.username");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
