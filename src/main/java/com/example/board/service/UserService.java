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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public UserDto.UserListResponse findUsers(
            Long id, String email, String name, String phone,
            String type, String address, String custom,
            int page, int limit, String sort) {

        int pageNum = Math.max(0, page - 1);
        
        Sort sortObj = Sort.by(Sort.Direction.DESC, "id");
        if (sort != null && sort.contains("createdAt")) {
            sortObj = Sort.by(Sort.Direction.ASC, "createdAt");
        } else if (sort != null && sort.contains("_id")) {
            sortObj = Sort.by(Sort.Direction.ASC, "id");
        }
        
        Pageable pageable = PageRequest.of(pageNum, limit, sortObj);
        
        Page<User> userPage;
        
        if (id != null) {
            userPage = userRepository.findById(id, pageable);
        } else if (email != null) {
            userPage = userRepository.findByEmailContaining(email, pageable);
        } else if (name != null) {
            userPage = userRepository.findByName(name, pageable);
        } else if (phone != null) {
            userPage = userRepository.findByPhone(phone, pageable);
        } else if (type != null) {
            userPage = userRepository.findByType(type, pageable);
        } else if (address != null) {
            userPage = userRepository.findByAddressContaining(address, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserDto.UserResponse> items = userPage.getContent().stream()
                .map(UserDto.UserResponse::fromEntity)
                .collect(Collectors.toList());

        return UserDto.UserListResponse.builder()
                .ok(1)
                .item(items)
                .pagination(UserDto.Pagination.builder()
                        .page(page)
                        .limit(limit)
                        .total(userPage.getTotalElements())
                        .totalPages(userPage.getTotalPages())
                        .build())
                .build();
    }
}