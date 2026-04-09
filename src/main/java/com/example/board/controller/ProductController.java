package com.example.board.controller;

import com.example.board.dto.ProductListResponse;
import com.example.board.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<ProductListResponse> list(
            @RequestParam(value = "minPrice", required = false) Integer minPrice,
            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(value = "minShippingFees", required = false) Integer minShippingFees,
            @RequestParam(value = "maxShippingFees", required = false) Integer maxShippingFees,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "seller_id", required = false) Long sellerId,
            @RequestParam(value = "custom", required = false) String custom,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "showSoldOut", required = false) Boolean showSoldOut
    ) {
        return ResponseEntity.ok(productService.findProducts(
                minPrice, maxPrice, minShippingFees, maxShippingFees,
                keyword, sellerId, page, limit, sort, showSoldOut));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductListResponse.ProductDetailResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }
}