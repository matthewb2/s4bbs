package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // @Data 대신 사용
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostItem {
    private Long _id;
    private String type;
    private String title;
    private String content;
    private String image;
    private String createdAt;
    private String updatedAt;

    private PostUser user;
    private Integer repliesCount;

    @Getter // 내부 클래스도 동일하게 수정
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostUser {
        private Long _id;
        private String name;
    }
}