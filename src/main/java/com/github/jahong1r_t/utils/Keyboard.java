package com.github.jahong1r_t.utils;

public interface Keyboard {
    String statisticsCmd = "Statistika \uD83D\uDCCA";
    String newMovieCmd = "Yangi film qo'shish \uD83C\uDFAC";
    String movieListCmd = "Filmlar ro'yxati \uD83D\uDCCB";
    String connectChannelCmd = "Kanal ulash \uD83D\uDD17";
    String sendToAllCmd = "Barchaga xabar yuborish \uD83D\uDD14";

    String[][] main_admin_keyboard = {
            {"Yangi film qo'shish \uD83C\uDFAC"},
            {"Statistika \uD83D\uDCCA", "Filmlar ro'yxati \uD83D\uDCCB"},
            {"Kanal ulash \uD83D\uDD17"},
            {"Barchaga xabar yuborish \uD83D\uDD14"}
    };

    String[][] stars = {
            {"⭐", "⭐", "⭐", "⭐", "⭐"}
    };


    static String[][] stars_data(String code) {
        return new String[][]{
                {
                        "rate:" + code + ":1",
                        "rate:" + code + ":2",
                        "rate:" + code + ":3",
                        "rate:" + code + ":4",
                        "rate:" + code + ":5"
                }
        };
    }

    String[][] admin_tools = {
            {"Taxrirlash ✏️", "O'chirish ❌"}
    };


    static String[][] admin_tools_data(String code) {
        return new String[][]{
                {
                        "tool:" + code + ":edit",
                        "tool:" + code + ":delete"
                }
        };
    }
}
