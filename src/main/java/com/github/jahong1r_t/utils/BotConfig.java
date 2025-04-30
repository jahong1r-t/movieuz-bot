package com.github.jahong1r_t.utils;

public interface BotConfig {
    String BOT_TOKEN = System.getenv("BOT_TOKEN");
    String BOT_USERNAME = System.getenv("BOT_USERNAME");
    Long BOT_ADMIN = Long.parseLong(System.getenv("BOT_ADMIN"));
    String DB_URL = System.getenv("DB_URL");
    String DB_USER = System.getenv("DB_USER");
    String DB_PASS = System.getenv("DB_PASSWORD");
}
