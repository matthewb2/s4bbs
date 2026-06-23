package com.example.board.controller;

import com.example.board.config.JwtTokenProvider;
import com.example.board.entity.User;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of(
                    "ok", 0,
                    "message", "인증 실패",
                    "errorName", "EmptyAuthorization"
            ));
        }

        String refreshToken = authHeader.substring(7);

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of(
                    "ok", 0,
                    "message", "인증 실패",
                    "errorName", "TokenExpiredError"
            ));
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String accessToken = jwtTokenProvider.generateAccessToken(userId, "user");
        String newRefreshToken = jwtTokenProvider.generateRefreshToken();

        return ResponseEntity.ok(Map.of(
                "ok", 1,
                "accessToken", accessToken,
                "refreshToken", newRefreshToken
        ));
    }

    @GetMapping("/email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam("token") String token) {
        User user = userRepository.findByEmailVerificationToken(token).orElse(null);
        if (user == null) {
            return ResponseEntity.status(400).body(Map.of(
                    "ok", 0,
                    "message", "유효하지 않은 토큰입니다."
            ));
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "ok", 1,
                "message", "이메일 인증이 완료되었습니다."
        ));
    }
}