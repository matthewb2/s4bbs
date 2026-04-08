package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateResponse {
    private int ok;
    private PostUpdateItem item;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostUpdateItem {
        private Long _id;
        private String title;
        private String content;
        private String updatedAt;
    }
}