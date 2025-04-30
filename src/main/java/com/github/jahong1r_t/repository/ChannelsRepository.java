package com.github.jahong1r_t.repository;

import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import static com.github.jahong1r_t.db.Datasource.*;

public class ChannelsRepository {
    private static Set<String> cachedChannels;

    static {
        cachedChannels = getAllChannels();
    }

    public Set<String> getAllChannel() {
        return cachedChannels;
    }

    public void updateCashedChannel() {
        cachedChannels = getAllChannels();
    }

    @SneakyThrows
    public void insertChannel(String link) {
        String sql = "INSERT INTO channels(link) VALUES(?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, normalizeChannelLink(link));
            ps.executeUpdate();
        }
        updateCashedChannel();
    }


    @SneakyThrows
    public void deleteChannel(String link) {
        String sql = "delete from channels where link = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, link);
            ps.executeUpdate();
        }
        updateCashedChannel();
    }


    @SneakyThrows
    public boolean isExist(String link) {
        return cachedChannels.contains(link);
    }


    @SneakyThrows
    private static Set<String> getAllChannels() {
        String sql = "select * from channels";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                Set<String> channels = new HashSet<>();
                while (rs.next()) {
                    channels.add(rs.getString("link"));
                }
                return channels;
            }
        }
    }

    private String normalizeChannelLink(String input) {
        if (input == null || input.isBlank()) return null;

        input = input.trim();

        if (input.startsWith("@")) {
            return "https://t.me/" + input.substring(1);
        }

        if (input.startsWith("https://t.me/")) {
            return input;
        }
        return "https://t.me/" + input;
    }


}
