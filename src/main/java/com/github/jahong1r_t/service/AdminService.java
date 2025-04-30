package com.github.jahong1r_t.service;

import com.github.jahong1r_t.entity.Movie;
import com.github.jahong1r_t.entity.User;
import com.github.jahong1r_t.entity.enums.State;
import com.github.jahong1r_t.repository.ChannelsRepository;
import com.github.jahong1r_t.repository.MoviesRepository;
import com.github.jahong1r_t.repository.UserRepository;
import com.github.jahong1r_t.utils.Utils;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.github.jahong1r_t.db.Datasource.*;
import static com.github.jahong1r_t.utils.Keyboard.*;
import static com.github.jahong1r_t.utils.Message.*;

public class AdminService {
    private final UserRepository userRepository = new UserRepository();
    private final MoviesRepository moviesRepository = new MoviesRepository();
    private final ChannelsRepository channelsRepository = new ChannelsRepository();
    private static Movie movieHolder = null;

    @SneakyThrows
    public void service(Update update, Utils utils) {
        Long chatId = update.getMessage().getChatId();
        State currentState = stateMap.getOrDefault(chatId, State.MAIN);

        switch (currentState) {
            case MAIN -> handleMainState(update, utils, chatId);
            case NEW_MOVIE -> handleNewMovieState(update, utils, chatId);
            case MOVIE_CODE -> handleMovieCodeState(update, utils, chatId);
            case CONNECT -> handleConnectChannelState(update, utils, chatId);
            case SEND_MSG -> handleSendMessageToAllState(update, utils, chatId);
            default -> utils.sendMessage(chatId, unknownStateMsg);
        }
    }

    private void handleMainState(Update update, Utils utils, Long chatId) {
        String text = update.getMessage().getText();

        switch (text) {
            case "/start" -> utils.sendMessage(chatId, welcomeAdminMsg, utils.keyboard(main_admin_keyboard));
            case statisticsCmd -> utils.sendMessage(chatId, getStatistics());
            case movieListCmd -> sendAllMovies(utils, chatId);
            case channelListCmd -> channelList(utils, chatId);
            case newMovieCmd -> {
                stateMap.put(chatId, State.NEW_MOVIE);
                utils.sendMessage(chatId, promptMovieUploadMsg);
            }
            case connectChannelCmd -> {
                stateMap.put(chatId, State.CONNECT);
                utils.sendMessage(chatId, promptChannelLinkMsg);
            }
            case sendToAllCmd -> {
                stateMap.put(chatId, State.SEND_MSG);
                utils.sendMessage(chatId, promptBroadcastMsg);
            }
            default -> utils.sendMessage(chatId, unknownCommandMsg);
        }
    }

    @SneakyThrows
    private void handleNewMovieState(Update update, Utils utils, Long chatId) {
        Message message = update.getMessage();

        if (message.hasText()) {
            String text = message.getText();
            if (isCommand(text)) {
                stateMap.put(chatId, State.MAIN);
                movieHolder = null;
                handleMainState(update, utils, chatId);
                return;
            }
        }

        if (!message.hasVideo()) {
            utils.sendMessage(chatId, videoRequiredMsg);
            return;
        }

        Video video = message.getVideo();
        String caption = message.getCaption();

        Movie movie = Movie.builder()
                .fileId(video.getFileId())
                .caption(caption)
                .download(0)
                .addedDate(LocalDateTime.now())
                .avgRate(0.0)
                .build();

        stateMap.put(chatId, State.MOVIE_CODE);
        movieHolder = movie;
        utils.sendMessage(chatId, promptMovieCodeEntryMsg);
    }

    private void handleMovieCodeState(Update update, Utils utils, Long chatId) {
        Message message = update.getMessage();

        if (message.hasText()) {
            String text = message.getText();
            if (isCommand(text)) {
                stateMap.put(chatId, State.MAIN);
                movieHolder = null;
                handleMainState(update, utils, chatId);
                return;
            }
        }

        String code = message.getText();

        if (movieHolder == null) {
            utils.sendMessage(chatId, movieDataNotFoundMsg);
            stateMap.put(chatId, State.MAIN);
            return;
        }


        if (!moviesRepository.isExist(code)) {
            movieHolder.setCode(code);
            moviesRepository.insertMovie(movieHolder);
            utils.sendMessage(chatId, movieAddedMsg);
            stateMap.put(chatId, State.MAIN);
            movieHolder = null;
        } else {
            utils.sendMessage(chatId, movieCodeExistsMsg);
        }
    }

    @SneakyThrows
    private void handleConnectChannelState(Update update, Utils utils, Long chatId) {
        Message message = update.getMessage();

        if (message.hasText()) {
            String text = message.getText();
            if (isCommand(text)) {
                stateMap.put(chatId, State.MAIN);
                movieHolder = null;
                handleMainState(update, utils, chatId);
                return;
            }
        }

        String channelLink = message.getText();

        channelsRepository.insertChannel(channelLink);

        try {
            if (utils.isBotAdmin(channelsRepository.getAllChannel())) {
                utils.sendMessage(chatId, channelConnectedMsg);
                stateMap.put(chatId, State.MAIN);
            } else {
                channelsRepository.deleteChannel(channelLink);
                utils.sendMessage(chatId, channelNotConnectedBotNotAdminMsg);
            }
        } catch (Exception e) {
            channelsRepository.deleteChannel(channelLink);
            utils.sendMessage(chatId, String.format(channelNotConnectedBotNotAdminMsg));
        }
    }

    @SneakyThrows
    private void handleSendMessageToAllState(Update update, Utils utils, Long chatId) {
        Message message = update.getMessage();
        List<User> users = userRepository.getAllUsers();

        if (message.hasText()) {
            String text = message.getText();
            if (isCommand(text)) {
                stateMap.put(chatId, State.MAIN);
                movieHolder = null;
                handleMainState(update, utils, chatId);
                return;
            }
        }

        if (!update.hasMessage()) {
            utils.sendMessage(chatId, messageRequiredMsg);
            return;
        }

        InlineKeyboardMarkup markup = message.getReplyMarkup();
        boolean sent = false;

        if (message.hasText()) {
            users.forEach((u) -> utils.sendMessage(u.getId(), message.getText(), markup));
            utils.sendMessage(chatId, broadcastTextSentMsg);
            sent = true;
        } else if (message.hasPhoto()) {
            List<PhotoSize> photos = message.getPhoto();
            String fileId = photos.get(photos.size() - 1).getFileId();
            users.forEach((u) -> utils.sendPhoto(u.getId(), fileId, message.getCaption(), markup));
            utils.sendMessage(chatId, broadcastPhotoSentMsg);
            sent = true;
        } else if (message.hasVideo()) {
            Video video = message.getVideo();
            users.forEach((u) -> utils.sendVideo(u.getId(), video.getFileId(), message.getCaption(), markup));
            utils.sendMessage(chatId, broadcastVideoSentMsg);
            sent = true;
        } else if (message.hasDocument()) {
            Document document = message.getDocument();
            users.forEach((u) -> utils.sendDocument(u.getId(), document.getFileId(), message.getCaption(), markup));
            utils.sendMessage(chatId, broadcastDocumentSentMsg);
            sent = true;
        } else if (message.hasAudio()) {
            Audio audio = message.getAudio();
            users.forEach((u) -> utils.sendAudio(u.getId(), audio.getFileId(), message.getCaption(), markup));
            utils.sendMessage(chatId, broadcastAudioSentMsg);
            sent = true;
        } else if (message.hasVoice()) {
            Voice voice = message.getVoice();
            users.forEach((u) -> utils.sendVoice(u.getId(), voice.getFileId(), markup));
            utils.sendMessage(chatId, broadcastVoiceSentMsg);
            sent = true;
        } else if (message.hasSticker()) {
            Sticker sticker = message.getSticker();
            users.forEach((u) -> utils.sendSticker(u.getId(), sticker.getFileId()));
            utils.sendMessage(chatId, broadcastStickerSentMsg);
            sent = true;
        }

        if (!sent) {
            utils.sendMessage(chatId, unsupportedMessageTypeMsg);
        } else {
            stateMap.put(chatId, State.MAIN);
        }
    }

    @SneakyThrows
    private boolean isCommand(String text) {
        return text.equals("/start") ||
                text.equals(statisticsCmd) ||
                text.equals(movieListCmd) ||
                text.equals(newMovieCmd) ||
                text.equals(connectChannelCmd) ||
                text.equals(channelListCmd) ||
                text.equals(sendToAllCmd);
    }

    @SneakyThrows
    private void channelList(Utils utils, Long chatId) {
        StringBuilder textBuilder = new StringBuilder();
        Set<String> channelsSet = channelsRepository.getAllChannel();

        if (channelsSet.isEmpty()) {
            utils.sendMessage(chatId, channelNotFound);
            return;
        }

        List<String> channels = new ArrayList<>(channelsSet);
        int size = channels.size();

        String[][] buttons = new String[size][1];
        String[][] data = new String[size][1];

        textBuilder.append("Ulangan kanallar ro'yxati 📃\n\n");

        for (int i = 0; i < size; i++) {
            buttons[i][0] = String.valueOf(i + 1);
            data[i][0] = channels.get(i);
            textBuilder.append(i + 1).append(". ").append(channels.get(i)).append("\n");
        }
        textBuilder.append("\n\n⚠️ Kanalni uzish uchun tanlang 👇");

        utils.sendMessage(chatId, textBuilder.toString(), utils.inlineKeyboard(buttons, data));
    }

    @SneakyThrows
    private String getStatistics() {
        StringBuilder sb = new StringBuilder("📊 Statistik ma'lumotlar:\n\n");

        sb.append("👥 Foydalanuvchilar:\n");
        sb.append("• Jami ro'yxatdan o'tgan foydalanuvchilar: ")
                .append(userRepository.countTotalUsers()).append(" ta\n");
        sb.append("• So'nggi 7 kun ichida faol bo'lganlar: ")
                .append(userRepository.countActiveUsers()).append(" ta\n");
        sb.append("• Bugun ro'yxatdan o'tgan foydalanuvchilar: ")
                .append(userRepository.countNewUsers()).append(" ta\n");

        userRepository.findMostActiveUser().ifPresent(user ->
                sb.append("• Eng faol foydalanuvchi: ")
                        .append(user.getUsername() != null ? "@" + user.getUsername() : "User#" + user.getId())
                        .append(" — ").append(user.getRequestCount()).append(" ta so'rov yuborgan\n")
        );

        sb.append("\n🎬 Filmlar:\n");
        sb.append("• Jami yuklangan filmlar: ")
                .append(moviesRepository.countTotalMovies()).append(" ta\n");
        sb.append("• Umumiy yuklanishlar soni: ")
                .append(moviesRepository.sumTotalDownloads()).append(" ta\n");

        List<Movie> topMovies = moviesRepository.findTopDownloadedMovies(3);
        sb.append("• Eng ko'p yuklangan 3 ta film:\n");
        for (int i = 0; i < 3; i++) {
            if (i < topMovies.size()) {
                sb.append("   ").append(i + 1).append(") ")
                        .append(topMovies.get(i).getCaption())
                        .append(" — ").append(topMovies.get(i).getDownload()).append(" ta yuklanish\n");
            } else {
                sb.append("   ").append(i + 1).append(") Ma'lumot yo'q\n");
            }
        }

        moviesRepository.findTopRatedMovie().ifPresent(movie ->
                sb.append("• Eng yuqori baholangan film: ")
                        .append(movie.getCaption())
                        .append(" — O'rtacha ").append(String.format("%.1f", movie.getAvgRate()))
                        .append(" yulduz\n")
        );

        moviesRepository.findLatestMovie().ifPresent(movie ->
                sb.append("• Eng so'nggi qo'shilgan film: ")
                        .append(movie.getCaption())
                        .append(" (")
                        .append(movie.getAddedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                        .append(")\n")
        );

        return sb.toString();
    }

    @SneakyThrows
    private void sendAllMovies(Utils utils, Long chatId) {
        ArrayList<String> messagePerPage = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();
        List<Movie> movies = moviesRepository.getAllMovies();

        List<Movie> sortedMovies = movies.stream()
                .sorted(Comparator.comparing(Movie::getAddedDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        StringBuilder pageBuilder = new StringBuilder();
        int movieInPageCount = 0;

        for (Movie movie : sortedMovies) {
            if (movieInPageCount > 0 && movieInPageCount % 10 == 0) {
                messagePerPage.add(pageBuilder.toString());
                pageBuilder = new StringBuilder();
                movieInPageCount = 0;
            }

            String caption = movie.getCaption() != null ? movie.getCaption() : "Noma'lum";
            String addedDate = movie.getAddedDate() != null ? movie.getAddedDate().toLocalDate().toString() : "Noma'lum";

            pageBuilder.append(movieInPageCount + 1).append(". ")
                    .append(caption).append(" (")
                    .append(addedDate).append(").\n");

            data.add(movie.getCode() != null ? movie.getCode() : "");
            movieInPageCount++;
        }

        if (!pageBuilder.isEmpty()) {
            messagePerPage.add(pageBuilder.toString());
        }

        utils.sendPaginationKeyboard(chatId, messagePerPage, data, 1, null, null);
    }
}
