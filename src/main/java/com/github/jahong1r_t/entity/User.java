package com.github.jahong1r_t.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {
    private Long id;
    private String username;
    private String fullName;
    private LocalDateTime joinDate;
    private LocalDateTime lastActivity;
    private int requestCount;
}
