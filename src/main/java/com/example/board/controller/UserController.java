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
}