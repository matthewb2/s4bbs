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
public class OrderCreateResponse {
    private int ok;
    private OrderItem item;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private List<ProductDetail> products;
        private String state;
        private Long user_id;
        private Long _id;
        private String createdAt;
        private String updatedAt;
        private Cost cost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDetail {
        private Long _id;
        private Integer quantity;
        private Long seller_id;
        private String name;
        private String size;
        private String color;
        private ImageItem image;
        private Integer price;
        private Object extra;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageItem {
        private String url;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cost {
        private Integer products;
        private Integer shippingFees;
        private Discount discount;
        private Integer total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {
        private Integer products;
        private Integer shippingFees;
    }
}