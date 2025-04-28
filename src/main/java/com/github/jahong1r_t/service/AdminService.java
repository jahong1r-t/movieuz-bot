package com.github.jahong1r_t.service;

import com.github.jahong1r_t.entity.Movie;
import com.github.jahong1r_t.entity.User;
import com.github.jahong1r_t.entity.enums.State;
import com.github.jahong1r_t.utils.Utils;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.github.jahong1r_t.db.Datasource.*;
import static com.github.jahong1r_t.utils.Keyboard.main_admin_keyboard;


public class AdminService {
    static Movie movie;

    @SneakyThrows
    public void service(Update update, Utils utils) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        State current = stateMap.getOrDefault(chatId, State.MAIN);


        if (current == State.MAIN) {
            switch (text) {
                case "/start" ->
                        utils.sendMessage(chatId, "Assalomu alaykum admin!", utils.keyboard(main_admin_keyboard));
                case "Yangi film qo'shish \uD83C\uDFAC" -> {
                    stateMap.put(chatId, State.NEW_MOVIE);
                    utils.sendMessage(chatId, "Kinoni menga yuboring.");
                }
                case "Statistika \uD83D\uDCCA" -> utils.sendMessage(chatId, getStatistics());

                case "Filmlar ro'yxati \uD83D\uDCCB" -> allMovies(utils, chatId);

                case "Kanal ulash \uD83D\uDD17" -> {
                    stateMap.put(chatId, State.CONNECT);
                    utils.sendMessage(chatId, "Kanal username yoki linkni yuboring");
                }
                case "Barchaga xabar yuborish \uD83D\uDD14" -> {
                    stateMap.put(chatId, State.SEND_MSG);
                    utils.sendMessage(chatId, "Barchaga xabar yuboring");
                }
            }
        } else if (current == State.NEW_MOVIE) {
            Video video = update.getMessage().getVideo();
            String caption = update.getMessage().getCaption();

            movie = Movie.builder()
                    .fileId(video.getFileId())
                    .caption(caption)
                    .download(0)
                    .addedDate(LocalDateTime.now())
                    .stars(new ArrayList<>())
                    .build();
            stateMap.put(chatId, State.MOVIE_CODE);
            utils.sendMessage(chatId, "Film kodini kiritng");

        } else if (current == State.MOVIE_CODE) {
            if (!movies.containsKey(text)) {
                movie.setCode(text);
                movies.put(text, movie);
                utils.sendMessage(chatId, "Yangi film joylandi.");
                stateMap.put(chatId, State.MAIN);
                movie = null;
            } else {
                utils.sendMessage(chatId, "Bunday kodli kino oldin ham joylangan. Boshqa kod bilan qayta urining.");
            }
        } else if (current == State.CONNECT) {
            channels.add(text);
            try {
                if (utils.isBotAdmin(channels)) {
                    utils.sendMessage(chatId, "Kanal ulandi.");
                    stateMap.put(chatId, State.MAIN);
                } else {
                    utils.sendMessage(chatId, "Kanal ulanmadi sababi bot kanalda admin emas.");
                    channels.remove(text);
                }
            } catch (Exception exception) {
                utils.sendMessage(chatId, "Kanal ulanmadi sababi bot kanalda admin emas.");
                System.err.println(exception.getMessage());
            }

        } else if (current == State.SEND_MSG) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                InlineKeyboardMarkup markup = message.getReplyMarkup();

                if (message.hasText()) {
                    users.forEach((i, u) -> utils.sendMessage(i, text, markup));
                    utils.sendMessage(chatId, "Matnli xabar barchaga yuborildi.");
                    stateMap.put(chatId, State.MAIN);
                } else if (message.hasPhoto()) {
                    String caption = message.getCaption();
                    List<PhotoSize> photos = message.getPhoto();
                    String fileId = photos.get(photos.size() - 1).getFileId();

                    users.forEach((i, u) -> utils.sendPhoto(i, fileId, caption, markup));
                    utils.sendMessage(chatId, "Rasm barchaga yuborildi.");
                    stateMap.put(chatId, State.MAIN);

                } else if (message.hasVideo()) {
                    String caption = message.getCaption();
                    Video video = message.getVideo();
                    String fileId = video.getFileId();

                    users.forEach((i, u) -> utils.sendVideo(i, fileId, caption, markup));
                    utils.sendMessage(chatId, "Video barchaga yuborildi.");
                    stateMap.put(chatId, State.MAIN);

                } else if (message.hasDocument()) {
                    String caption = message.getCaption();
                    Document document = message.getDocument();
                    String fileId = document.getFileId();

                    users.forEach((i, u) -> utils.sendDocument(i, fileId, caption, markup));
                    utils.sendMessage(chatId, "Hujjat barchaga yuborildi.");
                    stateMap.put(chatId, State.MAIN);

                } else if (message.hasAudio()) {
                    String caption = message.getCaption();
                    Audio audio = message.getAudio();
                    String fileId = audio.getFileId();

                    users.forEach((i, u) -> utils.sendAudio(i, fileId, caption, markup));
                    utils.sendMessage(chatId, "Audio barchaga yuborildi.");
                    stateMap.put(chatId, State.MAIN);

                } else if (message.hasVoice()) {
                    Voice voice = message.getVoice();
                    String fileId = voice.getFileId();

                    users.forEach((i, u) -> utils.sendVoice(i, fileId, markup));
                    utils.sendMessage(chatId, "Voice xabar barchaga yuborildi.");
                    stateMap.put(chatId, State.MAIN);


                } else if (message.hasSticker()) {
                    Sticker sticker = message.getSticker();
                    String fileId = sticker.getFileId();

                    users.forEach((i, u) -> utils.sendSticker(i, fileId));
                    utils.sendMessage(chatId, "Sticker barchaga yuborildi.");
                    stateMap.put(chatId, State.MAIN);

                } else {
                    utils.sendMessage(chatId, "Bu turdagi xabarni yuborish qo'llab-quvvatlanmaydi.");
                }
            }
        } else {
            utils.sendMessage(chatId, ".");
        }
    }

    @SneakyThrows
    private String getStatistics() {
        StringBuilder sb = new StringBuilder();

        sb.append("📊 Foydalanuvchilar statistikasi:\n")
                .append("Umumiy foydalanuvchilar: ").append(users.size()).append(" ta\n");

        long activeUsers = users.values().stream()
                .filter(u -> u.getLastActivity() != null &&
                        u.getLastActivity().isAfter(LocalDateTime.now().minusDays(7)))
                .count();
        sb.append("Oxirgi 7 kunda faol: ").append(activeUsers).append(" ta\n");

        long newUsers = users.values().stream()
                .filter(u -> u.getJoinDate() != null &&
                        u.getJoinDate().isAfter(LocalDateTime.now().minusDays(1)))
                .count();
        sb.append("Bugun qo'shilgan: ").append(newUsers).append(" ta\n");

        users.values().stream()
                .max(Comparator.comparingInt(User::getRequestCount)).ifPresent(topUser -> sb.append("Eng faol foydalanuvchi: ").append(topUser.getUsername() != null ? topUser.getUsername() : "User#" + topUser.getId())
                        .append(" (").append(topUser.getRequestCount()).append(" ta so'rov)\n"));

        sb.append("\n🎬 Filmlar statistikasi:\n")
                .append("Umumiy filmlar: ").append(movies.size()).append(" ta\n");

        long totalDownloads = movies.values().stream()
                .mapToLong(m -> m.getDownload() != null ? m.getDownload() : 0)
                .sum();
        sb.append("Umumiy yuklanishlar: ").append(totalDownloads).append(" ta\n");

        List<Movie> topMovies = movies.values().stream()
                .filter(m -> m.getDownload() != null)
                .sorted(Comparator.comparingInt(Movie::getDownload).reversed())
                .limit(3)
                .toList();
        sb.append("Eng ommabop filmlar:\n");
        for (int i = 0; i < topMovies.size(); i++) {
            sb.append(i + 1).append(". ").append(topMovies.get(i).getCaption())
                    .append(" (").append(topMovies.get(i).getDownload()).append(" yuklanish)\n");
        }
        for (int i = topMovies.size(); i < 3; i++) {
            sb.append(i + 1).append(". Mavjud emas\n");
        }

        Movie topRatedMovie = movies.values().stream()
                .filter(m -> m.getStars() != null && !m.getStars().isEmpty())
                .max(Comparator.comparingDouble(m -> m.getStars().stream().mapToDouble(Integer::doubleValue).average().orElse(0.0)))
                .orElse(null);
        if (topRatedMovie != null) {
            double avgRating = topRatedMovie.getStars().stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
            sb.append("Eng yuqori baholangan film: ").append(topRatedMovie.getCaption())
                    .append(" (O'rtacha ").append(String.format("%.1f", avgRating)).append(" yulduz)\n");
        } else {
            sb.append("Eng yuqori baholangan film: Ma'lumot yo'q\n");
        }

        Movie latestMovie = movies.values().stream()
                .filter(m -> m.getAddedDate() != null)
                .max(Comparator.comparing(Movie::getAddedDate))
                .orElse(null);
        if (latestMovie != null) {
            sb.append("So'nggi qo'shilgan film: ").append(latestMovie.getCaption())
                    .append(" (").append(latestMovie.getAddedDate().toLocalDate()).append(")\n");
        } else {
            sb.append("So'nggi qo'shilgan film: Ma'lumot yo'q\n");
        }

        return sb.toString();
    }

    @SneakyThrows
    private void allMovies(Utils utils, Long chatId) {
        ArrayList<String> messagePerPage = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();

        List<Movie> sortedMovies = movies.values().stream()
                .sorted(Comparator.comparing(Movie::getAddedDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        StringBuilder pageBuilder = new StringBuilder();
        int movieInPageCount = 0;

        for (int i = 0; i < sortedMovies.size(); i++) {
            Movie movie = sortedMovies.get(i);

            if (movieInPageCount > 0 && movieInPageCount % 10 == 0) {
                messagePerPage.add(pageBuilder.toString());
                pageBuilder = new StringBuilder();
                movieInPageCount = 0; // sahifa almashtik, 1 dan boshlaymiz
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
