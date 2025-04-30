package com.github.jahong1r_t.repository;

import com.github.jahong1r_t.entity.User;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.jahong1r_t.db.Datasource.*;

public class UserRepository {

    @SneakyThrows
    public void insertUser(User user) {
        String sql = "INSERT INTO users(id, username, full_name, join_date, last_activity, request_count) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getFullName());
            ps.setTimestamp(4, user.getJoinDate() != null ? Timestamp.valueOf(user.getJoinDate()) : null);
            ps.setTimestamp(5, user.getLastActivity() != null ? Timestamp.valueOf(user.getLastActivity()) : null);
            ps.setInt(6, user.getRequestCount());
            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public boolean isExist(Long id) {
        String sql = "SELECT id FROM users WHERE id = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
            return false;
        }
    }

    @SneakyThrows
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(
                            User.builder()
                                    .id(rs.getLong("id"))
                                    .username(rs.getString("username"))
                                    .fullName(rs.getString("full_name"))
                                    .joinDate(rs.getTimestamp("join_date").toLocalDateTime())
                                    .lastActivity(rs.getTimestamp("last_activity").toLocalDateTime())
                                    .requestCount(rs.getInt("request_count"))
                                    .build()
                    );
                }
                return users;
            }
        }
    }

    @SneakyThrows
    public void updateUserRequestCountById(Long id, LocalDateTime lastActivity) {
        String sql = "UPDATE users SET last_activity = ?, request_count = request_count + 1 WHERE id = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(lastActivity));
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public long countTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        }
    }

    @SneakyThrows
    public long countActiveUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE last_activity > NOW() - INTERVAL '7 days'";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        }
    }

    @SneakyThrows
    public long countNewUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE join_date > NOW() - INTERVAL '1 day'";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        }
    }

    @SneakyThrows
    public Optional<User> findMostActiveUser() {
        String sql = "SELECT id, username, request_count FROM users ORDER BY request_count DESC LIMIT 1";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(User.builder()
                            .id(rs.getLong("id"))
                            .username(rs.getString("username"))
                            .requestCount(rs.getInt("request_count"))
                            .build());
                }
                return Optional.empty();
            }
        }
    }
}
