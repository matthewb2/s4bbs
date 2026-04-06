package com.example.board.controller;

import com.example.board.dto.UserDto;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<UserDto.UserRegisterResponse> register(
            @Valid @RequestBody UserDto.UserRegisterRequest request
    ) {
        return ResponseEntity.status(201).body(userService.register(request));
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
}