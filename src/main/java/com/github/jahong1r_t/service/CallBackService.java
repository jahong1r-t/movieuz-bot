package com.github.jahong1r_t.service;

import com.github.jahong1r_t.entity.Movie;
import com.github.jahong1r_t.entity.User;
import com.github.jahong1r_t.entity.enums.State;
import com.github.jahong1r_t.repository.ChannelsRepository;
import com.github.jahong1r_t.repository.MoviesRepository;
import com.github.jahong1r_t.repository.UserRepository;
import com.github.jahong1r_t.utils.Message;
import com.github.jahong1r_t.utils.Utils;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.github.jahong1r_t.db.Datasource.*;
import static com.github.jahong1r_t.utils.Keyboard.admin_tools;
import static com.github.jahong1r_t.utils.Keyboard.admin_tools_data;

public class CallBackService {
    private final MoviesRepository moviesRepository = new MoviesRepository();
    private final UserRepository userRepository = new UserRepository();
    private final ChannelsRepository channelsRepository = new ChannelsRepository();

    @SneakyThrows
    public void service(CallbackQuery callbackQuery, Utils utils) {
        String data = callbackQuery.getData();
        org.telegram.telegrambots.meta.api.objects.User from = callbackQuery.getFrom();
        String id = callbackQuery.getId();
        Long chatId = callbackQuery.getFrom().getId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        if (data.equals("check")) {
            check(id, chatId, messageId, utils, from);
        } else if (data.startsWith("page")) {
            pagination(data, utils, chatId, messageId, id);
        } else if (data.startsWith("tool")) {
            tools(data, chatId, utils);
        } else if (data.startsWith("rate")) {
            rate(data, id, chatId, utils);
        } else if (data.startsWith("https://t.me")) {
            disconnectChannel(id, data, utils);
        } else {
            getMovie(data, chatId, utils);
        }
    }

    /// 100% done | checked
    @SneakyThrows
    private void check(String id, Long chatId, Integer messageId, Utils utils, org.telegram.telegrambots.meta.api.objects.User from) {
        if (utils.isChatMember(chatId, channelsRepository.getAllChannel())) {
            utils.deleteMessage(chatId, messageId);
            utils.sendMessage(chatId, Message.promptMovieCodeMsg);
            if (!userRepository.isExist(chatId)) {
                User user = User.builder()
                        .id(chatId)
                        .username(from.getUserName() != null
                                ? from.getUserName()
                                : "Nomalum")
                        .fullName(from.getFirstName() + " " +
                                (from.getLastName() != null
                                        ? from.getLastName()
                                        : ""))
                        .joinDate(LocalDateTime.now())
                        .lastActivity(LocalDateTime.now())
                        .requestCount(0)
                        .build();
                userRepository.insertUser(user);
            }
        } else {
            utils.answerCallbackQuery(id, Message.notFollowedChannelsMsg, true);
        }
    }

    /// 100% done | checked
    @SneakyThrows
    private void disconnectChannel(String id, String data, Utils utils) {
        if (channelsRepository.isExist(data)) {
            channelsRepository.deleteChannel(data);
            utils.answerCallbackQuery(id, Message.disconnectedMsg, false);
        }
    }

    /// 100% done | checked
    @SneakyThrows
    private void pagination(String data, Utils utils, Long chatId, Integer messageId, String id) {
        String pageData = data.replace("page:", "");
        int newPage;
        try {
            newPage = Integer.parseInt(pageData);
        } catch (NumberFormatException e) {
            utils.sendMessage(chatId, "Noto'g'ri sahifa raqami.");
            return;
        }

        ArrayList<String> messagePerPage = new ArrayList<>();
        ArrayList<String> datas = new ArrayList<>();
        List<Movie> sortedMovies = moviesRepository.getAllMovies().stream()
                .sorted(Comparator.comparing(Movie::getAddedDate, Comparator.nullsLast(Comparator.reverseOrder())))
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

            int displayNumber = (movieCount % 10) + 1;
            pageBuilder.append(displayNumber).append(". ").append(caption).append(" (").append(addedDate).append(").\n");

            datas.add(movie.getCode() != null ? movie.getCode() : "");

            movieCount++;
        }

        if (!pageBuilder.isEmpty()) {
            messagePerPage.add(pageBuilder.toString());
        }

        if (messagePerPage.isEmpty()) {
            messagePerPage.add("Filmlar topilmadi (Hozircha hech qanday film mavjud emas)");
            datas.add("");
        }

        if (newPage < 1 || newPage > messagePerPage.size()) {
            utils.sendMessage(chatId, "Noto'g'ri sahifa raqami.");
            return;
        }

        utils.sendPaginationKeyboard(chatId, messagePerPage, datas, newPage, messageId, id);
    }

    /// 100% done | checked
    @SneakyThrows
    private void tools(String data, Long chatId, Utils utils) {
        String[] split = data.split(":");

        String code = split[1];
        if (split[2].equals("edit")) {
            moviesRepository.deleteMovie(code);
            stateMap.put(chatId, State.NEW_MOVIE);
            utils.sendMessage(chatId, Message.promptMovieUploadMsg);
        } else if (split[2].equals("delete")) {
            moviesRepository.deleteMovie(code);
            utils.sendMessage(chatId, Message.movieRemovedMsg);
        }
    }

    ///100% done | checked
    @SneakyThrows
    private void rate(String data, String id, Long chatId, Utils utils) {
        String[] parts = data.split(":");
        String code = parts[1];
        int star = Integer.parseInt(parts[2]);

        Optional<Movie> movieByCode = moviesRepository.getMovieByCode(code);
        if (movieByCode.isPresent()) {
            try {
                moviesRepository.rateMovie(code, star, chatId);
                utils.answerCallbackQuery(id, Message.thankForRatingMsg, false);
            } catch (Exception e) {
                utils.answerCallbackQuery(id, Message.allReadyRatingMsg, false);
            }

        } else {
            utils.sendMessage(chatId, Message.movieNotFoundMsg);
        }
    }

    /// 100% done | checked
    @SneakyThrows
    private void getMovie(String data, Long chatId, Utils utils) {
        Optional<Movie> movieByCode = moviesRepository.getMovieByCode(data);
        if (movieByCode.isPresent()) {
            Movie movie = movieByCode.get();

            utils.sendVideo(chatId, captionBuilder(movie), movie.getFileId(),
                    utils.inlineKeyboard(admin_tools, admin_tools_data(movie.getCode())));
        } else {
            utils.sendMessage(chatId, Message.movieNotFoundMsg);
        }
    }

    /// 100% done | checked
    @SneakyThrows
    private String captionBuilder(Movie movie) {
        return """
                🎬 %s
                
                📥 Yuklab olingan: %d ta
                ⭐ O‘rtacha baho: %s
                📅 Yuklangan sana: %s
                🔑 Film kodi: %s
                
                @movieuz_kino_bot
                """.formatted(movie.getCaption(),
                movie.getDownload(),
                movie.getAvgRate(),
                movie.getAddedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                movie.getCode());
    }
}
