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
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static com.github.jahong1r_t.db.Datasource.stateMap;
import static com.github.jahong1r_t.utils.Keyboard.stars;
import static com.github.jahong1r_t.utils.Keyboard.stars_data;

public class UserService {
    private final UserRepository userRepository = new UserRepository();
    private final MoviesRepository moviesRepository = new MoviesRepository();
    private final ChannelsRepository channelsRepository = new ChannelsRepository();

    /// 100% done | checked
    @SneakyThrows
    public void service(Update update, Utils utils) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        State current = stateMap.getOrDefault(chatId, State.MAIN);

        if (current == State.MAIN) {

            Set<String> channels = channelsRepository.getAllChannel();

            if (!channels.isEmpty() && !utils.isChatMember(chatId, channels)) {
                utils.sendMessage(chatId, Message.welcomeUserMsg, utils.inlineKeyboardWithLink(channels));
                return;
            }

            if (text.equals("/start")) {
                if (!userRepository.isExist(chatId)) {
                    User user = User.builder()
                            .id(chatId)
                            .username(update.getMessage().getFrom().getUserName() != null
                                    ? update.getMessage().getFrom().getUserName()
                                    : "Nomalum")
                            .fullName(update.getMessage().getFrom().getFirstName() + " " +
                                    (update.getMessage().getFrom().getLastName() != null
                                            ? update.getMessage().getFrom().getLastName()
                                            : ""))
                            .joinDate(LocalDateTime.now())
                            .lastActivity(LocalDateTime.now())
                            .requestCount(0)
                            .build();
                    userRepository.insertUser(user);
                }

                utils.sendMessage(chatId, Message.promptMovieCodeMsg);
                return;
            }
            findMovie(text, utils, chatId);
        }
    }

    /// 100% done | checked
    @SneakyThrows
    private void findMovie(String text, Utils utils, Long chatId) {
        Optional<Movie> movieByCode = moviesRepository.getMovieByCode(text);

        if (movieByCode.isPresent()) {
            Movie movie = movieByCode.get();
            utils.sendVideo(chatId, captionBuilder(movie), movie.getFileId(),
                    utils.inlineKeyboard(stars, stars_data(movie.getCode())));

            userRepository.updateUserRequestCountById(chatId, LocalDateTime.now());
            moviesRepository.updateMovieDownload(text);

        } else {
            utils.sendMessage(chatId, Message.movieDataNotFoundMsg);
        }
    }

    /// 100% done | unchecked
    private String captionBuilder(Movie movie) {
        return """
                🎬 %s
                
                📥 Yuklab olingan: %d ta
                ⭐ O‘rtacha baho: %s
                
                P/S: Filmni 1 dan 5 gacha baholashni unitmang 👇
                @movieuz_kino_bot
                """.formatted(
                movie.getCaption(),
                movie.getDownload(),
                movie.getAvgRate()
        );
    }
}
