package com.github.jahong1r_t.repository;

import com.github.jahong1r_t.entity.Movie;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

import static com.github.jahong1r_t.db.Datasource.connection;

public class MoviesRepository {
    @SneakyThrows
    public boolean isExist(String code) {
        try (PreparedStatement ps = connection().prepareStatement("select * from movies where code=?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
            return false;
        }
    }

    @SneakyThrows
    public void deleteMovie(String code) {
        String sql = "DELETE FROM movies WHERE code = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public void insertMovie(Movie movie) {
        String sql = "INSERT INTO movies(code, file_id, caption, download, added_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, movie.getCode());
            ps.setString(2, movie.getFileId());
            ps.setString(3, movie.getCaption());
            ps.setInt(4, movie.getDownload());
            ps.setTimestamp(5, Timestamp.valueOf(movie.getAddedDate()));

            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public void rateMovie(String movieCode, int rate, Long userId) {
        String sql = "INSERT INTO movie_rates (movie_code, rating, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, movieCode);
            ps.setInt(2, rate);
            ps.setLong(3, userId);
            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public Optional<Movie> getMovieByCode(String code) {
        String sql = "SELECT m.code, m.file_id, m.caption, m.download, m.added_date, AVG(r.rating) AS avg_rate " +
                "FROM movies m LEFT JOIN movie_rates r ON m.code = r.movie_code " +
                "WHERE m.code = ? GROUP BY m.code, m.file_id, m.caption, m.download, m.added_date";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                            Movie.builder()
                                    .code(rs.getString("code"))
                                    .fileId(rs.getString("file_id"))
                                    .caption(rs.getString("caption"))
                                    .download(rs.getInt("download"))
                                    .addedDate(rs.getTimestamp("added_date").toLocalDateTime())
                                    .avgRate(rs.getDouble("avg_rate"))
                                    .build()
                    );
                }
                return Optional.empty();
            }
        }
    }

    @SneakyThrows
    public void updateMovieDownload(String code) {
        String sql = "UPDATE movies SET download = download + 1 WHERE code = ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public List<Movie> getAllMovies() {
        String sql = "SELECT * FROM movies";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Movie> movies = new ArrayList<>();
                while (rs.next()) {
                    movies.add(Movie.builder()
                            .code(rs.getString("code"))
                            .fileId(rs.getString("file_id"))
                            .caption(rs.getString("caption"))
                            .download(rs.getInt("download"))
                            .addedDate(rs.getTimestamp("added_date").toLocalDateTime())
                            .avgRate(rs.getDouble("avg_rate"))
                            .build());
                }
                return movies;
            }
        }
    }

    @SneakyThrows
    public long countTotalMovies() {
        String sql = "SELECT COUNT(*) FROM movies";
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
    public long sumTotalDownloads() {
        String sql = "SELECT SUM(download) FROM movies";
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
    public List<Movie> findTopDownloadedMovies(int limit) {
        String sql = "SELECT code, caption, download FROM movies WHERE download IS NOT NULL " +
                "ORDER BY download DESC LIMIT ?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Movie> movies = new ArrayList<>();
                while (rs.next()) {
                    movies.add(Movie.builder()
                            .code(rs.getString("code"))
                            .caption(rs.getString("caption"))
                            .download(rs.getInt("download"))
                            .build());
                }
                return movies;
            }
        }
    }

    @SneakyThrows
    public Optional<Movie> findMostRatedAndTopMovie() {
        String sql = "SELECT m.code, m.caption, COUNT(r.rating) AS rating_count, AVG(r.rating) AS avg_rating " +
                "FROM movies m " +
                "JOIN movie_rates r ON m.code = r.movie_code " +
                "GROUP BY m.code, m.caption " +
                "ORDER BY rating_count DESC, avg_rating DESC LIMIT 1";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Movie.builder()
                            .code(rs.getString("code"))
                            .caption(rs.getString("caption"))
                            .avgRate(rs.getDouble("avg_rating"))
                            .build());
                }
                return Optional.empty();
            }
        }
    }


    @SneakyThrows
    public Optional<Movie> findLatestMovie() {
        String sql = "SELECT code, caption, added_date FROM movies " +
                "WHERE added_date IS NOT NULL ORDER BY added_date DESC LIMIT 1";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Movie.builder()
                            .code(rs.getString("code"))
                            .caption(rs.getString("caption"))
                            .addedDate(rs.getTimestamp("added_date").toLocalDateTime())
                            .build());
                }
                return Optional.empty();
            }
        }
    }
}
