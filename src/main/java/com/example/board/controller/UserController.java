package com.example.board.controller;

import com.example.board.dto.UserDto;
import com.example.board.service.KakaoService;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @GetMapping("/email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.checkEmail(email));
    }


    @GetMapping("/name")
    public ResponseEntity<Map<String, Object>> checkName(@RequestParam String name) {
        return ResponseEntity.ok(userService.checkName(name));
    }

    @PostMapping("/")
    public ResponseEntity<UserDto.UserRegisterResponse> register(
            @Valid @RequestBody UserDto.UserRegisterRequest request
    ) {
        return ResponseEntity.status(201).body(userService.register(request));
    }

    @PostMapping("/signup/oauth")
    public ResponseEntity<UserDto.UserRegisterResponse> oauthSignup(
            @Valid @RequestBody UserDto.OAuthSignupRequest request
    ) {
        return ResponseEntity.status(201).body(userService.oauthSignup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.LoginResponse> login(
            @Valid @RequestBody UserDto.LoginRequest request
    ) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/login/kakao")
    public ResponseEntity<UserDto.KakaoLoginResponse> kakaoLogin(@RequestBody UserDto.KakaoLoginRequest request) {
        return ResponseEntity.ok(kakaoService.login(request));
    }

    @GetMapping("/")
    public ResponseEntity<UserDto.UserListResponse> list(
            @RequestParam(value = "_id", required = false) Long id,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "custom", required = false) String custom,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        return ResponseEntity.ok(userService.findUsers(id, email, name, phone, type, address, custom, page, limit, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto.UserRegisterResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto.UserUpdateResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto.UserUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @GetMapping("/{id}/{field}")
    public ResponseEntity<Map<String, Object>> getUserField(
            @PathVariable Long id,
            @PathVariable String field
    ) {
        return ResponseEntity.ok(userService.getUserField(id, field));
    }
}