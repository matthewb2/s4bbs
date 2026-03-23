package com.example.board.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
public class PostListResponse {
    private int ok; // 성공 시 1
    private List<PostItem> item;
    private Pagination pagination;

    @Data
    @Builder
    public static class Pagination {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }
}