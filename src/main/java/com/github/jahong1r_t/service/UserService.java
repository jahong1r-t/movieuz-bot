package com.github.jahong1r_t.service;

import com.github.jahong1r_t.entity.*;
import com.github.jahong1r_t.entity.enums.State;
import com.github.jahong1r_t.utils.Message;
import com.github.jahong1r_t.utils.Utils;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

import static com.github.jahong1r_t.db.Datasource.*;
import static com.github.jahong1r_t.utils.Keyboard.stars;
import static com.github.jahong1r_t.utils.Keyboard.stars_data;

public class UserService {

    @SneakyThrows
    public void service(Update update, Utils utils) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        State current = stateMap.getOrDefault(chatId, State.MAIN);

        if (current == State.MAIN) {
            if (!channels.isEmpty()) {
                if (!utils.isChatMember(chatId, channels)) {
                    utils.sendMessage(chatId, Message.welcomeUserMsg, utils.inlineKeyboardWithLink(channels));
                    return;
                }
            }

            if (text.equals("/start")) {
                users.putIfAbsent(chatId, User.builder()
                        .id(chatId)
                        .username(update.getMessage().getFrom().getUserName() != null ? update.getMessage().getFrom().getUserName() : "Nomalum")
                        .fullName(update.getMessage().getFrom().getFirstName() + " " + (update.getMessage().getFrom().getLastName() != null ? update.getMessage().getFrom().getLastName() : ""))
                        .joinDate(LocalDateTime.now())
                        .lastActivity(LocalDateTime.now())
                        .requestCount(0)
                        .build());

                utils.sendMessage(chatId, Message.promptMovieCodeMsg);
            } else {
                findMovie(text, utils, chatId);
            }
        }
    }

    @SneakyThrows
    private void findMovie(String text, Utils utils, Long chatId) {
        Movie movie = movies.get(text);
        if (movie != null) {
            utils.sendVideo(chatId, captionBuilder(movie), movie.getFileId(), utils.inlineKeyboard(stars, stars_data(movie.getCode())));

            User user = users.get(chatId);

            user.setRequestCount(user.getRequestCount() + 1);
            user.setLastActivity(LocalDateTime.now());
            movie.setDownload(movie.getDownload() + 1);
        } else {
            utils.sendMessage(chatId, Message.movieNotFoundMsg);
        }
    }

    @SneakyThrows
    private String captionBuilder(Movie movie) {
        String averageStars;

        if (movie.getStars().isEmpty()) {
            averageStars = "Film uchun baholar qoldirilmagan";
        } else {
            double average = movie.getStars().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            averageStars = String.format("%.1f", average);
        }

        return """
                🎬 %s
                
                📥 Yuklab olingan: %d ta
                ⭐ O‘rtacha baho: %s
                
                P/S: Filmni 1 dan 5 gacha  baholashni unitmang 👇
                @movieuz_kino_bot
                """.formatted(
                movie.getCaption(),
                movie.getDownload(),
                averageStars
        );
    }
}
