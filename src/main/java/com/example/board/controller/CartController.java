package com.example.board.controller;

import com.example.board.dto.CartRequest;
import com.example.board.dto.CartResponse;
import com.example.board.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/local")
    public ResponseEntity<CartResponse> getLocalCart(@RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.getLocalCart(request.getProducts()));
    }
}