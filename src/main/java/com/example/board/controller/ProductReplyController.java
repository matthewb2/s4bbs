package com.example.board.controller;

import com.example.board.config.JwtTokenProvider;
import com.example.board.dto.ProductReplyRequest;
import com.example.board.dto.ProductReplyResponse;
import com.example.board.service.ProductReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ProductReplyController {

    private final ProductReplyService productReplyService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/")
    public ResponseEntity<ProductReplyResponse> create(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ProductReplyRequest request
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.status(201).body(productReplyService.createReply(request, userId));
    }

    @GetMapping("/")
    public ResponseEntity<ProductReplyResponse> getMyReplies(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "full_name", required = false) Boolean fullName
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        boolean full = fullName != null && fullName;
        return ResponseEntity.ok(productReplyService.getMyReplies(userId, full));
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