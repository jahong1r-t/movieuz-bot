package com.github.jahong1r_t.bot;

import com.github.jahong1r_t.service.AdminService;
import com.github.jahong1r_t.service.CallBackService;
import com.github.jahong1r_t.service.UserService;
import com.github.jahong1r_t.utils.Utils;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.jahong1r_t.utils.BotConfig.*;

public class MainBot extends TelegramLongPollingBot {
    private final AdminService adminService = new AdminService();
    private final UserService userService = new UserService();
    private final Utils utils = new Utils(this);
    private final CallBackService callBackService = new CallBackService();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().getChatId().equals(BOT_ADMIN_1) || update.getMessage().getChatId().equals(BOT_ADMIN_2)) {
                adminService.service(update, utils);
            } else {
                userService.service(update, utils);
            }
        } else if (update.hasCallbackQuery()) {
            callBackService.service(update.getCallbackQuery(), utils);
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
