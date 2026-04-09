package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    private int ok;
    private List<ProductItem> item;
    private Pagination pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        private Long _id;
        private Long seller_id;
        private Integer price;
        private Integer shippingFees;
        private Boolean show;
        private Boolean active;
        private String name;
        private Integer quantity;
        private Integer buyQuantity;
        private List<ImageItem> mainImages;
        private String content;
        private String createdAt;
        private String updatedAt;
        private Object extra;
        private SellerItem seller;
        private Integer replies;
        private Integer bookmarks;
        private Double rating;
        private Long myBookmarkId;
        private Integer options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageItem {
        private String path;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerItem {
        private Long _id;
        private String email;
        private String name;
        private String phone;
        private String address;
        private String image;
        private Object extra;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDetailResponse {
        private int ok;
        private ProductItem item;
    }
}