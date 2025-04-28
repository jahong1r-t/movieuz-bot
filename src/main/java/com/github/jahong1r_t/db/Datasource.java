package com.github.jahong1r_t.db;

import com.github.jahong1r_t.entity.Movie;
import com.github.jahong1r_t.entity.User;
import com.github.jahong1r_t.entity.enums.State;

import java.util.*;

public class Datasource {
    public static Map<Long, State> stateMap = new HashMap<>();
    public static Map<String, Movie> movies = new HashMap<>();
    public static Map<Long, User> users = new HashMap<>();
    public static Set<String> channels = new HashSet<>();
}
