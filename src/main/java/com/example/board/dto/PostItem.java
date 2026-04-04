package com.example.board.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostItem {
    private Long _id;
    private String type;
    private String title;
    private String content;
    private String image;
    private String createdAt;
    private String updatedAt;

    // Map 대신 전용 Static 클래스 또는 외부 DTO 사용
    private PostUser user;
    private Integer repliesCount;

    @Data
    @Builder
    public static class PostUser {
        private Long _id;
        private String name;
    }
}