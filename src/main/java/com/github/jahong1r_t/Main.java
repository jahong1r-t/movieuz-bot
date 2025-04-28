package com.github.jahong1r_t;

import com.github.jahong1r_t.bot.MainBot;
import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.InetSocketAddress;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        MainBot mainBot = new MainBot();
        api.registerBot(mainBot);
        mainBot.keepServerAwake();

        new Thread(() -> {
            try {
                int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                server.createContext("/", httpExchange -> {
                    String response = "Bot is running.";
                    httpExchange.sendResponseHeaders(200, response.getBytes().length);
                    httpExchange.getResponseBody().write(response.getBytes());
                    httpExchange.close();
                });
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("Bot is running.");
    }
}
