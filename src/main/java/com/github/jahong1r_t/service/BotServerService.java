package com.github.jahong1r_t.service;


import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotServerService {
    @SneakyThrows
    public void service() {
        keepServerAwake();
        startHttpServer();
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @SneakyThrows
    public void keepServerAwake() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                URL url = new URL("https://movieuz-bot.onrender.com");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                System.out.println("Keep server awake: " + conn.getResponseCode());
            } catch (Exception e) {
                System.err.println("Ping error keep server awake: " + e.getMessage());
            }
        }, 0, 12, TimeUnit.MINUTES);
    }

    @SneakyThrows
    public void startHttpServer() {
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
                System.out.println("HTTP server started on port " + port);
            } catch (Exception e) {
                System.err.println("Ping error: " + e.getMessage());
            }
        }).start();
    }
}
