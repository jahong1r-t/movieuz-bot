package com.github.jahong1r_t.utils;

import com.github.jahong1r_t.exceptions.BotNotAdminException;
import com.github.jahong1r_t.exceptions.InvalidChannelLinkException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.*;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@AllArgsConstructor
public class Utils {
    private final AbsSender bot;
    private static final SendMessage sendMessage = new SendMessage();
    private static final SendVideo sendVideo = new SendVideo();
    private static final DeleteMessage deleteMessage = new DeleteMessage();
    private static final InputFile inputFile = new InputFile();
    private static final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    private static final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    private static final AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();

    @SneakyThrows
    public void sendMessage(Long chatId, String text) {
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        bot.execute(sendMessage);

    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboard);
        bot.execute(sendMessage);
    }

    @SneakyThrows
    public void sendVideo(Long chatId, String caption, String fileId, ReplyKeyboard replyKeyboard) {
        sendVideo.setChatId(chatId);
        sendVideo.setCaption(caption);
        sendVideo.setVideo(inputFile.setMedia(fileId));
        sendVideo.setReplyMarkup(replyKeyboard);
        bot.execute(sendVideo);
    }

    @SneakyThrows
    public void deleteMessage(Long chatId, Integer messageId) {
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        bot.execute(deleteMessage);
    }

    @SneakyThrows
    public void answerCallbackQuery(String id, String text, boolean showAlert) {
        AnswerCallbackQuery build = AnswerCallbackQuery.builder()
                .callbackQueryId(id)
                .text(text)
                .showAlert(showAlert)
                .build();

        bot.execute(build);
    }

    @SneakyThrows
    public ReplyKeyboard keyboard(String[][] buttons) {
        List<KeyboardRow> rows = Arrays.stream(buttons)
                .map(row -> {
                    KeyboardRow keyboardRow = new KeyboardRow();
                    keyboardRow.addAll(Arrays.asList(row));
                    return keyboardRow;
                })
                .collect(Collectors.toList());

        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }

    @SneakyThrows
    public ReplyKeyboard inlineKeyboard(String[][] buttons, String[][] data) {

        List<List<InlineKeyboardButton>> rows =
                IntStream.range(0, buttons.length)
                        .mapToObj(i -> IntStream.range(0, buttons[i].length)
                                .mapToObj(j -> InlineKeyboardButton.builder()
                                        .callbackData(data[i][j])
                                        .text(buttons[i][j])
                                        .build())
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList());

        markup.setKeyboard(rows);

        return markup;
    }

    public InlineKeyboardMarkup inlineKeyboardWithLink(Set<String> urls) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int index = 1;
        for (String url : urls) {
            String buttonText = index + "-Kanal";

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(InlineKeyboardButton.builder()
                    .url(linkResolverHttps(url))
                    .text(buttonText)
                    .build());

            rows.add(row);
            index++;
        }

        List<InlineKeyboardButton> confirmationRow = new ArrayList<>();
        confirmationRow.add(InlineKeyboardButton.builder()
                .callbackData("check")
                .text("Tasdiqlash ✅")
                .build());
        rows.add(confirmationRow);

        markup.setKeyboard(rows);
        return markup;
    }

    @SneakyThrows
    public void sendPaginationKeyboard(Long chatId, ArrayList<String> messagePerPage, ArrayList<String> data, int currentPage, Integer messageId, String callbackQueryId) {
        if (data == null || data.isEmpty() || messagePerPage == null || messagePerPage.isEmpty()) {
            sendMessage(chatId, "Hech qanday ma'lumot topilmadi.");
            if (callbackQueryId != null) {
                bot.execute(AnswerCallbackQuery.builder()
                        .callbackQueryId(callbackQueryId)
                        .text("Ma'lumot yo'q")
                        .build());
            }
            return;
        }

        if (currentPage < 1 || currentPage > messagePerPage.size()) {
            sendMessage(chatId, "Noto'g'ri sahifa raqami.");
            if (callbackQueryId != null) {
                bot.execute(AnswerCallbackQuery.builder()
                        .callbackQueryId(callbackQueryId)
                        .text("Noto'g'ri sahifa raqami")
                        .build());
            }
            return;
        }

        int maxPage = (int) Math.ceil((double) data.size() / 10);
        String messageText = messagePerPage.get(currentPage - 1);

        InlineKeyboardMarkup markup = buildPaginationKeyboard(currentPage, maxPage, data, chatId);

        if (messageId == null) {
            if (markup.getKeyboard().isEmpty()) {
                sendMessage(chatId, "Filmlar topilmadi (Hozircha hech qanday film mavjud emas)");
                return;
            }

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(messageText);
            sendMessage.setReplyMarkup(markup);
            bot.execute(sendMessage);
            if (callbackQueryId != null) {
                bot.execute(AnswerCallbackQuery.builder()
                        .callbackQueryId(callbackQueryId)
                        .build());
            }
        } else {
            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);
            editMessage.setText(messageText);
            editMessage.setReplyMarkup(markup);

            try {
                bot.execute(editMessage);
            } catch (TelegramApiRequestException e) {
                if (e.getMessage().contains("Boshqa sahifalar mavjud emas ❌")) {
                    if (callbackQueryId != null) {
                        bot.execute(AnswerCallbackQuery.builder()
                                .callbackQueryId(callbackQueryId)
                                .build());
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    @SneakyThrows
    private InlineKeyboardMarkup buildPaginationKeyboard(int currentPage, int maxPage, ArrayList<String> data, Long chatId) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        int itemsPerPage = 10;
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, data.size());

        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            String callbackData = data.get(i);

            if (callbackData == null || callbackData.isEmpty()) {
                sendMessage(chatId, "Filmlar topilmadi (Hozircha hech qanday film mavjud emas)");
                return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
            } else if (callbackData.length() > 64) {
                callbackData = callbackData.substring(0, 64);
            } else {
                callbackData = callbackData.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            }

            int buttonNumber = (i % 10) + 1;
            row.add(InlineKeyboardButton.builder()
                    .text(String.valueOf(buttonNumber))
                    .callbackData(callbackData)
                    .build());

            if (row.size() == 5) {
                keyboard.add(new ArrayList<>(row));
                row.clear();
            }
        }

        if (!row.isEmpty()) {
            keyboard.add(new ArrayList<>(row));
        }

        List<InlineKeyboardButton> navigationButtons = new ArrayList<>();

        if (currentPage > 1) {
            int prevPage = currentPage - 1;
            navigationButtons.add(InlineKeyboardButton.builder().text("⬅️").callbackData("page:" + prevPage).build());
        }

        if (currentPage < maxPage) {
            int nextPage = currentPage + 1;
            navigationButtons.add(InlineKeyboardButton.builder().text("➡️").callbackData("page:" + nextPage).build());
        }

        if (!navigationButtons.isEmpty()) {
            keyboard.add(navigationButtons);
        }

        return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
    }

    @SneakyThrows
    public boolean isBotAdmin(Set<String> chats) {
        User botUser = bot.execute(new GetMe());
        Long botId = botUser.getId();

        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setUserId(botId);

        for (String s : chats) {
            getChatMember.setChatId(linkResolver(s));
            ChatMember member = bot.execute(getChatMember);

            if (!(member instanceof ChatMemberAdministrator)) {
                return false;
            }
        }
        return true;
    }

    @SneakyThrows
    public boolean isChatMember(Long userId, Set<String> chats) {
        if (isBotAdmin(chats)) {
            for (String chat : chats) {
                ChatMember execute = bot.execute(GetChatMember.builder()
                        .chatId(linkResolver(chat))
                        .userId(userId)
                        .build());

                if (!execute.getStatus().equals("member")) {
                    return false;
                }
            }

            return true;
        } else {
            throw new BotNotAdminException("Bot is not admin of this chat: ");
        }
    }

    @SneakyThrows
    private static String linkResolver(String link) {
        if (link == null || link.isBlank()) {
            throw new InvalidChannelLinkException("Channel identifier cannot be null or empty");
        }

        if (link.startsWith("@")) {
            return link;
        }

        if (link.startsWith("https://t.me/")) {
            String username = link.substring("https://t.me/".length());
            if (username.isEmpty()) {
                throw new InvalidChannelLinkException("No username found in Telegram link");
            }
            return "@" + username;
        }
        return "@" + link;
    }

    @SneakyThrows
    private static String linkResolverHttps(String link) {
        if (link == null || link.isBlank()) {
            throw new InvalidChannelLinkException("Channel identifier cannot be null or empty");
        }

        if (link.startsWith("@")) {
            return "https://t.me/" + link.substring("@".length());
        } else if (link.startsWith("https://t.me/")) {
            return link;
        }

        return "https://t.me/" + link;
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        if (markup != null) {
            sendMessage.setReplyMarkup(markup);
        }
        bot.execute(sendMessage);
    }

    @SneakyThrows
    public void sendPhoto(Long chatId, String fileId, String caption, InlineKeyboardMarkup markup) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(fileId));
        sendPhoto.setCaption(caption);
        if (markup != null) {
            sendPhoto.setReplyMarkup(markup);
        }
        bot.execute(sendPhoto);
    }

    @SneakyThrows
    public void sendVideo(Long chatId, String fileId, String caption, InlineKeyboardMarkup markup) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId.toString());
        sendVideo.setVideo(new InputFile(fileId));
        sendVideo.setCaption(caption);
        if (markup != null) {
            sendVideo.setReplyMarkup(markup);
        }
        bot.execute(sendVideo);
    }


    @SneakyThrows
    public void sendDocument(Long chatId, String fileId, String caption, InlineKeyboardMarkup markup) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId.toString());
        sendDocument.setDocument(new InputFile(fileId));
        sendDocument.setCaption(caption);
        if (markup != null) {
            sendDocument.setReplyMarkup(markup);
        }
        bot.execute(sendDocument);
    }

    @SneakyThrows
    public void sendAudio(Long chatId, String fileId, String caption, InlineKeyboardMarkup markup) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId.toString());
        sendAudio.setAudio(new InputFile(fileId));
        sendAudio.setCaption(caption);
        if (markup != null) {
            sendAudio.setReplyMarkup(markup);
        }
        bot.execute(sendAudio);
    }

    @SneakyThrows
    public void sendVoice(Long chatId, String fileId, ReplyKeyboard markup) {
        SendVoice sendVoice = new SendVoice();
        sendVoice.setChatId(chatId.toString());
        sendVoice.setVoice(new InputFile(fileId));
        if (markup != null) {
            sendVoice.setReplyMarkup(markup);
        }
        bot.execute(sendVoice);
    }

    @SneakyThrows
    public void sendSticker(Long chatId, String fileId) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(chatId.toString());
        sendSticker.setSticker(new InputFile(fileId));
        bot.execute(sendSticker);
    }
}
