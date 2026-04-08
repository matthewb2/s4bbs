package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponse {
    private int ok;
    private ReplyItem item;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyItem {
        private String content;
        private ReplyUser user;
        private Long _id;
        private String createdAt;
        private String updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyUser {
        private Long _id;
        private String name;
        private String image;
    }
}