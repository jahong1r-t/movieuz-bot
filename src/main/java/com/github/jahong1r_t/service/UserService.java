package com.github.jahong1r_t.service;

import com.github.jahong1r_t.entity.Movie;
import com.github.jahong1r_t.entity.User;
import com.github.jahong1r_t.entity.enums.State;
import com.github.jahong1r_t.utils.Utils;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

import static com.github.jahong1r_t.db.Datasource.*;

public class UserService {

    @SneakyThrows
    public void service(Update update, Utils utils) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        State current = stateMap.getOrDefault(chatId, State.MAIN);

        if (current == State.MAIN) {
            if (!channels.isEmpty()) {
                if (!utils.isChatMember(chatId, channels)) {
                    utils.sendMessage(chatId, "Botimizdan to'liq foydalanish uchun telegram kanalga obuna bo'ling va Tekshirish tugmasini bosing.", utils.inlineKeyboardWithLink(channels));
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

                utils.sendMessage(chatId, "Salom kino kodini kirit");
            } else {
                findMovie(text, utils, chatId);
            }
        }
    }

    private void findMovie(String text, Utils utils, Long chatId) {
        Movie movie = movies.get(text);

        if (movie != null) {
            utils.sendVideo(chatId, movie);
            User user = users.get(chatId);

            user.setRequestCount(user.getRequestCount() + 1);
            user.setLastActivity(LocalDateTime.now());
            movie.setDownload(movie.getDownload() + 1);
        } else {
            utils.sendMessage(chatId, "Afsuski bunday film topilmadi \uD83D\uDE14. Qayta urinib ko'ring !");
        }
    }
}
