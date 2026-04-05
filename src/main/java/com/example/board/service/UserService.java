package com.example.board.service;

import com.example.board.dto.UserDto;
import com.example.board.entity.User;
import com.example.board.global.CustomException;
import com.example.board.global.ErrorCode;
import com.example.board.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto.UserRegisterResponse register(UserDto.UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .type(request.getType())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .image(request.getImage())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        if (request.getExtra() != null) {
            try {
                user.setExtra(objectMapper.writeValueAsString(request.getExtra()));
            } catch (JsonProcessingException e) {
                throw new CustomException(ErrorCode.INTERNAL_ERROR);
            }
        }

        User saved = userRepository.save(user);

        return UserDto.UserRegisterResponse.builder()
                .ok(1)
                .item(UserDto.UserResponse.fromEntity(saved))
                .build();
    }
}