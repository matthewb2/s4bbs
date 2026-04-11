package com.example.board.service;

import com.example.board.dto.ProductReplyRequest;
import com.example.board.dto.ProductReplyResponse;
import com.example.board.entity.Product;
import com.example.board.entity.ProductReply;
import com.example.board.entity.User;
import com.example.board.repository.ProductReplyRepository;
import com.example.board.repository.ProductRepository;
import com.example.board.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReplyService {

    private final ProductReplyRepository productReplyRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ProductReplyResponse createReply(ProductReplyRequest request, Long userId) {
        ProductReply reply = ProductReply.builder()
                .orderId(request.getOrder_id())
                .productId(request.getProduct_id())
                .rating(request.getRating())
                .content(request.getContent())
                .userId(userId)
                .build();

        if (request.getExtra() != null) {
            try {
                reply.setExtra(objectMapper.writeValueAsString(request.getExtra()));
            } catch (JsonProcessingException e) {
                // ignore
            }
        }

        ProductReply saved = productReplyRepository.save(reply);

        return ProductReplyResponse.builder()
                .ok(1)
                .item(ProductReplyResponse.Item.builder()
                        ._id(saved.getId())
                        .order_id(saved.getOrderId())
                        .product_id(saved.getProductId())
                        .rating(saved.getRating())
                        .content(saved.getContent())
                        .extra(saved.getExtra())
                        .user_id(userId)
                        .createdAt(format(saved.getCreatedAt()))
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public ProductReplyResponse getMyReplies(Long userId, boolean fullName) {
        List<ProductReply> replies = productReplyRepository.findByUserId(userId);

        List<ProductReplyResponse.Item> items = replies.stream()
                .map(reply -> {
                    ProductReplyResponse.Item.ItemBuilder builder = ProductReplyResponse.Item.builder()
                            ._id(reply.getId())
                            .rating(reply.getRating())
                            .content(reply.getContent())
                            .createdAt(format(reply.getCreatedAt()));

                    User user = userRepository.findById(reply.getUserId()).orElse(null);
                    if (user != null) {
                        String name = fullName ? user.getName() : maskName(user.getName());
                        builder.user(ProductReplyResponse.User.builder()
                                ._id(user.getId())
                                .image(user.getImage())
                                .name(name)
                                .build());
                    }

                    Product product = productRepository.findById(reply.getProductId()).orElse(null);
                    if (product != null) {
                        ProductReplyResponse.ImageItem imageItem = null;
                        if (product.getMainImages() != null) {
                            try {
                                List<Map<String, String>> images = objectMapper.readValue(product.getMainImages(), new TypeReference<>() {});
                                if (!images.isEmpty()) {
                                    imageItem = ProductReplyResponse.ImageItem.builder()
                                            .path(images.get(0).get("path"))
                                            .name(images.get(0).get("name"))
                                            .build();
                                }
                            } catch (Exception e) {
                                // ignore
                            }
                        }
                        builder.product(ProductReplyResponse.Product.builder()
                                ._id(product.getId())
                                .image(imageItem)
                                .name(product.getName())
                                .build());
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());

        return ProductReplyResponse.builder()
                .ok(1)
                .itemList(items)
                .build();
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 1) return name;
        return name.charAt(0) + "**";
    }

    private String format(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}