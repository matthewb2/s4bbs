package com.example.board.service;

import com.example.board.dto.ProductListResponse;
import com.example.board.entity.Product;
import com.example.board.entity.User;
import com.example.board.repository.ProductRepository;
import com.example.board.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public ProductListResponse findProducts(
            Integer minPrice, Integer maxPrice,
            Integer minShippingFees, Integer maxShippingFees,
            String keyword, Long sellerId,
            int page, int limit, String sort, Boolean showSoldOut) {

        int pageNum = Math.max(0, page - 1);
        int pageLimit = limit > 0 ? limit : 100;

        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sort != null && sort.contains("price")) {
            if (sort.contains("-1")) {
                sortObj = Sort.by(Sort.Direction.DESC, "price");
            } else {
                sortObj = Sort.by(Sort.Direction.ASC, "price");
            }
        }

        Pageable pageable = PageRequest.of(pageNum, pageLimit, sortObj);

        Page<Product> productPage;
        
        if (keyword != null && sellerId != null) {
            productPage = productRepository.findBySellerIdAndNameContaining(sellerId, keyword, pageable);
        } else if (keyword != null) {
            if (minPrice != null || maxPrice != null) {
                int min = minPrice != null ? minPrice : 0;
                int max = maxPrice != null ? maxPrice : Integer.MAX_VALUE;
                productPage = productRepository.findByNameContainingAndPriceBetween(keyword, min, max, pageable);
            } else {
                productPage = productRepository.findByNameContaining(keyword, pageable);
            }
        } else if (sellerId != null) {
            productPage = productRepository.findBySellerId(sellerId, pageable);
        } else if (minPrice != null || maxPrice != null) {
            int min = minPrice != null ? minPrice : 0;
            int max = maxPrice != null ? maxPrice : Integer.MAX_VALUE;
            productPage = productRepository.findByPriceBetween(min, max, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductListResponse.ProductItem> items = productPage.getContent().stream()
                .map(this::toProductItem)
                .collect(Collectors.toList());

        return ProductListResponse.builder()
                .ok(1)
                .item(items)
                .pagination(ProductListResponse.Pagination.builder()
                        .page(page)
                        .limit(limit)
                        .total(productPage.getTotalElements())
                        .totalPages(productPage.getTotalPages())
                        .build())
                .build();
    }

    private ProductListResponse.ProductItem toProductItem(Product product) {
        List<ProductListResponse.ImageItem> images = null;
        if (product.getMainImages() != null) {
            try {
                images = objectMapper.readValue(product.getMainImages(), new TypeReference<>() {});
            } catch (Exception e) {
                images = null;
            }
        }

        Object extra = null;
        if (product.getExtra() != null) {
            try {
                extra = objectMapper.readValue(product.getExtra(), Object.class);
            } catch (Exception e) {
                extra = null;
            }
        }

        ProductListResponse.SellerItem seller = null;
        if (product.getSellerId() != null) {
            User sellerUser = userRepository.findById(product.getSellerId()).orElse(null);
            if (sellerUser != null) {
                seller = ProductListResponse.SellerItem.builder()
                        ._id(sellerUser.getId())
                        .email(sellerUser.getEmail())
                        .name(sellerUser.getName())
                        .phone(sellerUser.getPhone())
                        .address(sellerUser.getAddress())
                        .image(sellerUser.getImage())
                        .build();
            }
        }

        return ProductListResponse.ProductItem.builder()
                ._id(product.getId())
                .seller_id(product.getSellerId())
                .price(product.getPrice())
                .shippingFees(product.getShippingFees())
                .show(product.getShow())
                .active(product.getActive())
                .name(product.getName())
                .quantity(product.getQuantity())
                .buyQuantity(product.getBuyQuantity())
                .mainImages(images)
                .content(product.getContent())
                .createdAt(format(product.getCreatedAt()))
                .updatedAt(format(product.getUpdatedAt()))
                .extra(extra)
                .seller(seller)
                .replies(0)
                .bookmarks(0)
                .rating(0.0)
                .options(0)
                .build();
    }

    @Transactional(readOnly = true)
    public ProductListResponse.ProductDetailResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductListResponse.ProductItem item = toProductItem(product);

        return ProductListResponse.ProductDetailResponse.builder()
                .ok(1)
                .item(item)
                .build();
    }

    private String format(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}