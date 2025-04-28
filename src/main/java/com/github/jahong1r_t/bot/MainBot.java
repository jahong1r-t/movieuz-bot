package com.github.jahong1r_t.bot;

import com.github.jahong1r_t.entity.Movie;
import com.github.jahong1r_t.service.AdminService;
import com.github.jahong1r_t.service.UserService;
import com.github.jahong1r_t.utils.BotConfig;
import com.github.jahong1r_t.utils.Utils;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.github.jahong1r_t.db.Datasource.channels;
import static com.github.jahong1r_t.db.Datasource.movies;

public class MainBot extends TelegramLongPollingBot {
    private final BotConfig botConfig = new BotConfig();
    private final AdminService adminService = new AdminService();
    private final UserService userService = new UserService();
    private final Utils utils = new Utils(this);


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().getChatId().equals(botConfig.BOT_ADMIN)) {
                adminService.service(update, utils);
            } else {
                userService.service(update, utils);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            Long chatId = callbackQuery.getFrom().getId();
            Integer messageId = callbackQuery.getMessage().getMessageId();
            String id = callbackQuery.getId();

            if (callbackData.equals("check")) {
                returnAlertMessage(callbackQuery);
            } else if (callbackData.startsWith("page:")) {
                handleCallback(callbackData, chatId, messageId, id);
            } else if (callbackData.startsWith("cd_")) {
                if (!callbackQuery.getFrom().getId().equals(botConfig.BOT_ADMIN)){
                    String[] parts = callbackData.split("_");
                    String movieCode = parts[1];
                    String number = parts[2];
                    movies.get(movieCode).getStars().add(Integer.parseInt(number));
                    AnswerCallbackQuery build = AnswerCallbackQuery.builder()
                            .showAlert(true)
                            .callbackQueryId(callbackQuery.getId())
                            .text("Filmni baxolaganingiz uchun raxmat.")
                            .build();
                    execute(build);
                }else {
                    utils.sendMessage(botConfig.BOT_ADMIN, "Bu tugma siz uchun emas!");
                }
            } else {
                System.err.println(callbackData);
                Movie movie = movies.get(callbackData);
                if (movie != null) {
                    utils.sendVideo(chatId, movie);
                } else {
                    utils.sendMessage(chatId, "Film topilmadi.");
                }
            }
        }
    }

    @SneakyThrows
    public void handleCallback(String callbackData, Long chatId, Integer messageId, String id) {
        String pageData = callbackData.replace("page:", "");
        int newPage;
        try {
            newPage = Integer.parseInt(pageData);
        } catch (NumberFormatException e) {
            utils.sendMessage(chatId, "Noto'g'ri sahifa raqami.");
            return;
        }

        ArrayList<String> messagePerPage = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();

        List<Movie> sortedMovies = movies.values().stream()
                .sorted(Comparator.comparing(Movie::getCaption))
                .toList();

        StringBuilder pageBuilder = new StringBuilder();
        int movieCount = 0;

        for (Movie movie : sortedMovies) {
            if (movieCount > 0 && movieCount % 10 == 0) {
                messagePerPage.add(pageBuilder.toString());
                pageBuilder = new StringBuilder();
            }

            String caption = movie.getCaption() != null ? movie.getCaption() : "Noma'lum";
            String addedDate = movie.getAddedDate() != null ? movie.getAddedDate().toLocalDate().toString() : "Noma'lum";

            pageBuilder.append(movieCount + 1).append(". ")
                    .append(caption).append(" (")
                    .append(addedDate).append(").\n");

            data.add(movie.getFileId() != null ? movie.getFileId() : "");

            movieCount++;
        }

        if (!pageBuilder.isEmpty()) {
            messagePerPage.add(pageBuilder.toString());
        }

        if (messagePerPage.isEmpty()) {
            messagePerPage.add("Filmlar topilmadi (Hozircha hech qanday film mavjud emas)");
            data.add("");
        }

        if (newPage < 1 || newPage > messagePerPage.size()) {
            utils.sendMessage(chatId, "Noto'g'ri sahifa raqami.");
            return;
        }

        utils.sendPaginationKeyboard(chatId, messagePerPage, data, newPage, messageId, id);
    }

    @SneakyThrows
    public void returnAlertMessage(CallbackQuery callbackQuery) {
        AnswerCallbackQuery build = AnswerCallbackQuery.builder()
                .text("❌ Kechirasiz siz barcha kanallarga a'zo bo'lmadingiz")
                .showAlert(true)
                .callbackQueryId(callbackQuery.getId())
                .build();
        Long chatId = callbackQuery.getFrom().getId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        if (utils.isChatMember(chatId, channels)) {
            execute(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
            utils.sendMessage(chatId, "Salom kino kodini yubor");
        } else {
            execute(build);
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return botConfig.BOT_TOKEN;
    }
}
