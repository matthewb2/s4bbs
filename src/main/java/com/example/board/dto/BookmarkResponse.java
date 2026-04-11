package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {
    private int ok;
    private Item item;
    private List<Item> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String type;
        private Long user_id;
        private Long target_id;
        private String memo;
        private Long _id;
        private String createdAt;
        private ProductItem product;
        private UserItem user;
        private PostItem post;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        private Long _id;
        private String name;
        private int price;
        private int quantity;
        private int buyQuantity;
        private ImageItem image;
        private java.util.Map<String, Object> extra;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserItem {
        private Long _id;
        private String name;
        private boolean delete;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostItem {
        private Long _id;
        private String subject;
        private String content;
        private int hit;
        private UserItem user;
        private java.util.Map<String, Object> extra;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageItem {
        private String url;
        private String name;
    }
}