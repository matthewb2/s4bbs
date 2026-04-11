package com.example.board.controller;

import com.example.board.config.JwtTokenProvider;
import com.example.board.dto.OrderCreateRequest;
import com.example.board.dto.OrderCreateResponse;
import com.example.board.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/")
    public ResponseEntity<OrderCreateResponse> create(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody OrderCreateRequest request
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.status(201).body(orderService.createOrder(request, userId));
    }

    private Long getUserIdFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getUserIdFromToken(token);
            }
        }
        return 4L;
    }
}