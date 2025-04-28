package com.github.jahong1r_t.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Movie {
    private String fileId;
    private String caption;
    private String code;
    private Integer download;
    private LocalDateTime addedDate;
    private ArrayList<Integer> stars;
}
