package com.example.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateResponse {

    private int ok;
    private Item item;

    @Getter
    @Builder
    public static class Item {

        private String type;
        private String title;
        private String content;
        private String image;
        private String tag;
        private Integer views;

        private User user;

        private Long _id;
        private String createdAt;
        private String updatedAt;
    }

    @Getter
    @Builder
    public static class User {
        private Long _id;
        private String name;
    }
}
