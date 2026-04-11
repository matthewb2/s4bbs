package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReplyRequest {
    private Long order_id;
    private Long product_id;
    private Integer rating;
    private String content;
    private Map<String, Object> extra;
}