package com.github.jahong1r_t;

import com.github.jahong1r_t.bot.MainBot;
import com.github.jahong1r_t.utils.BotConfig;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        MainBot mainBot = new MainBot();
        mainBot.keepServerAwake();
        api.registerBot(mainBot);
    }
}
