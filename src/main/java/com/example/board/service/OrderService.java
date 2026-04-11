package com.example.board.service;

import com.example.board.dto.OrderCreateRequest;
import com.example.board.dto.OrderCreateResponse;
import com.example.board.entity.Order;
import com.example.board.entity.Product;
import com.example.board.repository.OrderRepository;
import com.example.board.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest request, Long userId) {
        if (request.getProducts() == null || request.getProducts().isEmpty()) {
            throw new RuntimeException("Products are required");
        }

        List<OrderCreateResponse.ProductDetail> productDetails = new ArrayList<>();
        int productsCost = 0;
        int shippingFees = 0;

        for (OrderCreateRequest.ProductOrder productOrder : request.getProducts()) {
            if (productOrder.get_id() == null) {
                throw new RuntimeException("Product ID is required");
            }

            Product product = productRepository.findById(productOrder.get_id())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productOrder.get_id()));

            int price = product.getPrice() != null ? product.getPrice() : 0;
            int qty = productOrder.getQuantity() != null ? productOrder.getQuantity() : 1;
            int productTotal = price * qty;
            productsCost += productTotal;
            shippingFees += product.getShippingFees() != null ? product.getShippingFees() : 0;

            OrderCreateResponse.ProductDetail detail = OrderCreateResponse.ProductDetail.builder()
                    ._id(product.getId())
                    .quantity(qty)
                    .seller_id(product.getSellerId())
                    .name(product.getName())
                    .size(productOrder.getSize())
                    .color(productOrder.getColor())
                    .price(price)
                    .build();

            if (product.getMainImages() != null) {
                try {
                    List<Map<String, String>> images = objectMapper.readValue(product.getMainImages(), new TypeReference<>() {});
                    if (!images.isEmpty()) {
                        detail.setImage(OrderCreateResponse.ImageItem.builder()
                                .url(images.get(0).get("path"))
                                .name(images.get(0).get("name"))
                                .build());
                    }
                } catch (JsonProcessingException e) {
                    // ignore
                }
            }

            if (product.getExtra() != null) {
                try {
                    detail.setExtra(objectMapper.readValue(product.getExtra(), Object.class));
                } catch (JsonProcessingException e) {
                    // ignore
                }
            }

            productDetails.add(detail);
        }

        int discountProducts = (int) (productsCost * 0.1);
        int discountShippingFees = shippingFees > 0 ? shippingFees : 0;
        int total = productsCost + shippingFees - discountProducts - discountShippingFees;

        Order order = Order.builder()
                .userId(userId)
                .state("OS020")
                .productsCost(productsCost)
                .shippingFees(shippingFees)
                .discountProducts(discountProducts)
                .discountShippingFees(discountShippingFees)
                .total(total)
                .build();

        try {
            order.setProducts(objectMapper.writeValueAsString(request.getProducts()));
        } catch (JsonProcessingException e) {
            // ignore
        }

        Order saved = orderRepository.save(order);

        return OrderCreateResponse.builder()
                .ok(1)
                .item(OrderCreateResponse.OrderItem.builder()
                        .products(productDetails)
                        .state(saved.getState())
                        .user_id(userId)
                        ._id(saved.getId())
                        .createdAt(format(saved.getCreatedAt()))
                        .updatedAt(format(saved.getUpdatedAt()))
                        .cost(OrderCreateResponse.Cost.builder()
                                .products(productsCost)
                                .shippingFees(shippingFees)
                                .discount(OrderCreateResponse.Discount.builder()
                                        .products(discountProducts)
                                        .shippingFees(discountShippingFees)
                                        .build())
                                .total(total)
                                .build())
                        .build())
                .build();
    }

    private String format(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}