package com.github.jahong1r_t;

import com.github.jahong1r_t.bot.MainBot;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    @SneakyThrows
    public static void main(String[] args) throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        MainBot mainBot = new MainBot();

        String webhookUrl = "https://movieuz-bot.onrender.com/webhook";
        SetWebhook setWebhook = new SetWebhook();
        setWebhook.setUrl(webhookUrl);
        api.registerBot(mainBot, setWebhook);
        mainBot.keepServerAwake();
        System.out.println("Bot started in Webhook mode with URL: " + webhookUrl);
    }
}
