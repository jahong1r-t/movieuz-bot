package com.github.jahong1r_t.db;

import com.github.jahong1r_t.entity.Movie;
import com.github.jahong1r_t.entity.User;
import com.github.jahong1r_t.entity.enums.State;

import java.time.LocalDateTime;
import java.util.*;

public class Datasource {
    public static Map<Long, State> stateMap = new HashMap<>();
    public static Map<String, Movie> movies = new HashMap<>();
    public static Map<Long, User> users = new HashMap<>();
    public static Set<String> channels = new HashSet<>();

    static {
        movies.put("MOV001", Movie.builder()
                .caption("Inception\nA thief who steals corporate secrets through dream infiltration.")
                .fileId("FILE_ID_001")
                .code("MOV001")
                .download(500)
                .addedDate(LocalDateTime.of(2024, 4, 25, 10, 0))
                .stars(new ArrayList<>(Arrays.asList(4, 5, 5)))
                .build());

        movies.put("MOV002", Movie.builder()
                .caption("Interstellar\nA team travels through a wormhole in space to ensure humanity's survival.")
                .fileId("FILE_ID_002")
                .code("MOV002")
                .download(320)
                .addedDate(LocalDateTime.of(2024, 4, 26, 11, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 5)))
                .build());

        movies.put("MOV003", Movie.builder()
                .caption("The Matrix\nA computer hacker learns about the true nature of his reality.")
                .fileId("FILE_ID_003")
                .code("MOV003")
                .download(450)
                .addedDate(LocalDateTime.of(2024, 4, 27, 12, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 4, 5)))
                .build());

        movies.put("MOV004", Movie.builder()
                .caption("The Dark Knight\nBatman faces the Joker in Gotham City.")
                .fileId("FILE_ID_004")
                .code("MOV004")
                .download(780)
                .addedDate(LocalDateTime.of(2024, 4, 28, 13, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 5)))
                .build());

        movies.put("MOV005", Movie.builder()
                .caption("Avatar\nA paraplegic Marine is dispatched to the moon Pandora.")
                .fileId("FILE_ID_005")
                .code("MOV005")
                .download(610)
                .addedDate(LocalDateTime.of(2024, 4, 29, 14, 0))
                .stars(new ArrayList<>(Arrays.asList(4, 4, 5)))
                .build());

        movies.put("MOV006", Movie.builder()
                .caption("Titanic\nA seventeen-year-old aristocrat falls in love with a kind but poor artist.")
                .fileId("FILE_ID_006")
                .code("MOV006")
                .download(900)
                .addedDate(LocalDateTime.of(2024, 4, 30, 15, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 5)))
                .build());

        movies.put("MOV007", Movie.builder()
                .caption("Gladiator\nA former Roman General sets out to exact vengeance.")
                .fileId("FILE_ID_007")
                .code("MOV007")
                .download(520)
                .addedDate(LocalDateTime.of(2024, 5, 1, 16, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 4, 4)))
                .build());

        movies.put("MOV008", Movie.builder()
                .caption("Forrest Gump\nThe life journey of a slow-witted but kind-hearted man.")
                .fileId("FILE_ID_008")
                .code("MOV008")
                .download(430)
                .addedDate(LocalDateTime.of(2024, 5, 2, 17, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 4)))
                .build());

        movies.put("MOV009", Movie.builder()
                .caption("The Godfather\nThe aging patriarch of an organized crime dynasty transfers control to his son.")
                .fileId("FILE_ID_009")
                .code("MOV009")
                .download(1000)
                .addedDate(LocalDateTime.of(2024, 5, 3, 18, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 5)))
                .build());

        movies.put("MOV010", Movie.builder()
                .caption("Pulp Fiction\nThe lives of two mob hitmen intertwine in unexpected ways.")
                .fileId("FILE_ID_010")
                .code("MOV010")
                .download(700)
                .addedDate(LocalDateTime.of(2024, 5, 4, 19, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 4, 5)))
                .build());

        movies.put("MOV011", Movie.builder()
                .caption("Avengers: Endgame\nThe Avengers assemble once more to reverse Thanos' actions.")
                .fileId("FILE_ID_011")
                .code("MOV011")
                .download(1500)
                .addedDate(LocalDateTime.of(2024, 5, 5, 20, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 5)))
                .build());

        movies.put("MOV012", Movie.builder()
                .caption("Spider-Man: No Way Home\nPeter Parker seeks help from Doctor Strange.")
                .fileId("FILE_ID_012")
                .code("MOV012")
                .download(950)
                .addedDate(LocalDateTime.of(2024, 5, 6, 21, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 4, 5)))
                .build());

        movies.put("MOV013", Movie.builder()
                .caption("Joker\nA mentally troubled comedian embarks on a downward spiral.")
                .fileId("FILE_ID_013")
                .code("MOV013")
                .download(870)
                .addedDate(LocalDateTime.of(2024, 5, 7, 22, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 4)))
                .build());

        movies.put("MOV014", Movie.builder()
                .caption("Parasite\nGreed and class discrimination threaten the newly formed symbiotic relationship.")
                .fileId("FILE_ID_014")
                .code("MOV014")
                .download(540)
                .addedDate(LocalDateTime.of(2024, 5, 8, 23, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 5)))
                .build());

        movies.put("MOV015", Movie.builder()
                .caption("1917\nTwo young British soldiers during WWI are given an impossible mission.")
                .fileId("FILE_ID_015")
                .code("MOV015")
                .download(300)
                .addedDate(LocalDateTime.of(2024, 5, 9, 9, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 4, 5)))
                .build());

        movies.put("MOV016", Movie.builder()
                .caption("The Shawshank Redemption\nTwo imprisoned men bond over years, finding solace.")
                .fileId("FILE_ID_016")
                .code("MOV016")
                .download(1100)
                .addedDate(LocalDateTime.of(2024, 5, 10, 10, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 5)))
                .build());

        movies.put("MOV017", Movie.builder()
                .caption("Fight Club\nAn insomniac office worker forms an underground fight club.")
                .fileId("FILE_ID_017")
                .code("MOV017")
                .download(750)
                .addedDate(LocalDateTime.of(2024, 5, 11, 11, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 4, 5)))
                .build());

        movies.put("MOV018", Movie.builder()
                .caption("The Lion King\nLion prince Simba and his father are targeted by his bitter uncle.")
                .fileId("FILE_ID_018")
                .code("MOV018")
                .download(600)
                .addedDate(LocalDateTime.of(2024, 5, 12, 12, 0))
                .stars(new ArrayList<>(Arrays.asList(4, 5, 5)))
                .build());

        movies.put("MOV019", Movie.builder()
                .caption("Toy Story\nA cowboy doll feels threatened by a new spaceman toy.")
                .fileId("FILE_ID_019")
                .code("MOV019")
                .download(580)
                .addedDate(LocalDateTime.of(2024, 5, 13, 13, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 4, 4)))
                .build());

        movies.put("MOV020", Movie.builder()
                .caption("Finding Nemo\nAfter his son is captured, a timid clownfish sets out to find him.")
                .fileId("FILE_ID_020")
                .code("MOV020")
                .download(640)
                .addedDate(LocalDateTime.of(2024, 5, 14, 14, 0))
                .stars(new ArrayList<>(Arrays.asList(5, 5, 4)))
                .build());


        // Mock Users
        users.put(1L, new User(1L, "john_doe", "John Doe",
                LocalDateTime.of(2024, 3, 1, 8, 0),
                LocalDateTime.of(2024, 4, 28, 9, 0), 50));
        users.put(2L, new User(2L, "jane_smith", "Jane Smith",
                LocalDateTime.of(2024, 3, 5, 10, 0),
                LocalDateTime.of(2024, 4, 27, 12, 0), 30));
        users.put(3L, new User(3L, "bob_jones", "Bob Jones",
                LocalDateTime.of(2024, 3, 10, 11, 0),
                LocalDateTime.of(2024, 4, 26, 15, 0), 25));
        users.put(4L, new User(4L, "alice_brown", "Alice Brown",
                LocalDateTime.of(2024, 3, 15, 9, 0),
                LocalDateTime.of(2024, 4, 28, 10, 0), 40));
        users.put(5L, new User(5L, "charlie_wilson", "Charlie Wilson",
                LocalDateTime.of(2024, 3, 20, 14, 0),
                LocalDateTime.of(2024, 4, 25, 8, 0), 15));
        users.put(6L, new User(6L, "diana_clark", "Diana Clark",
                LocalDateTime.of(2024, 3, 25, 12, 0),
                LocalDateTime.of(2024, 4, 27, 16, 0), 20));
        users.put(7L, new User(7L, "emma_davis", "Emma Davis",
                LocalDateTime.of(2024, 4, 1, 13, 0),
                LocalDateTime.of(2024, 4, 28, 11, 0), 10));
        users.put(8L, new User(8L, "frank_martin", "Frank Martin",
                LocalDateTime.of(2024, 4, 5, 15, 0),
                LocalDateTime.of(2024, 4, 26, 14, 0), 5));
        users.put(9L, new User(9L, "grace_lee", "Grace Lee",
                LocalDateTime.of(2024, 4, 10, 16, 0),
                LocalDateTime.of(2024, 4, 27, 9, 0), 8));
        users.put(10L, new User(10L, "henry_kim", "Henry Kim",
                LocalDateTime.of(2024, 4, 15, 10, 0),
                LocalDateTime.of(2024, 4, 28, 12, 0), 12));
    }
}
