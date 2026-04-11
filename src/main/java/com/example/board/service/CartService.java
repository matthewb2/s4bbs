package com.example.board.service;

import com.example.board.dto.CartRequest;
import com.example.board.dto.CartResponse;
import com.example.board.entity.Product;
import com.example.board.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public CartResponse getLocalCart(List<CartRequest.ProductItem> products) {
        List<CartResponse.CartProduct> cartProducts = new ArrayList<>();
        int productsCost = 0;
        int shippingFees = 0;

        for (CartRequest.ProductItem item : products) {
            Product product = productRepository.findById(item.get_id()).orElse(null);
            if (product == null) continue;

            int price = product.getPrice() != null ? product.getPrice() : 0;
            int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
            int productTotal = price * quantity;
            productsCost += productTotal;
            shippingFees += product.getShippingFees() != null ? product.getShippingFees() : 0;

            CartResponse.CartProduct cartProduct = CartResponse.CartProduct.builder()
                    ._id(product.getId())
                    .quantity(quantity)
                    .quantityInStock(product.getQuantity())
                    .seller_id(product.getSellerId())
                    .name(product.getName())
                    .price(price)
                    .build();

            if (product.getMainImages() != null) {
                try {
                    List<Map<String, String>> images = objectMapper.readValue(product.getMainImages(), new TypeReference<>() {});
                    if (!images.isEmpty()) {
                        cartProduct.setImage(CartResponse.ImageItem.builder()
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
                    cartProduct.setExtra(objectMapper.readValue(product.getExtra(), Object.class));
                } catch (JsonProcessingException e) {
                    // ignore
                }
            }

            cartProducts.add(cartProduct);
        }

        int discountProducts = (int) (productsCost * 0.1);
        int discountShippingFees = shippingFees > 0 ? shippingFees : 0;
        int total = productsCost + shippingFees - discountProducts - discountShippingFees;

        return CartResponse.builder()
                .ok(1)
                .item(CartResponse.CartItem.builder()
                        .products(cartProducts)
                        .cost(CartResponse.Cost.builder()
                                .products(productsCost)
                                .shippingFees(shippingFees)
                                .discount(CartResponse.Discount.builder()
                                        .products(discountProducts)
                                        .shippingFees(discountShippingFees)
                                        .build())
                                .total(total)
                                .build())
                        .build())
                .build();
    }
}