package com.github.jahong1r_t.db;

import com.github.jahong1r_t.entity.enums.State;
import com.github.jahong1r_t.utils.BotConfig;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

public class Datasource {
    public static Map<Long, State> stateMap = new HashMap<>();
    private static Connection connection;

    @SneakyThrows
    public static Connection connection() {
        if (connection == null || connection.isClosed()) {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    BotConfig.DB_URL,
                    BotConfig.DB_USER,
                    BotConfig.DB_PASS
            );
        }
        return connection;
    }
}
