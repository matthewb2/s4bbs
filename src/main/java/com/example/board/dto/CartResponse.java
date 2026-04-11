package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private int ok;
    private CartItem item;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItem {
        private List<CartProduct> products;
        private Cost cost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartProduct {
        private Long _id;
        private Integer quantity;
        private Integer quantityInStock;
        private Long seller_id;
        private String name;
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