package com.example.board.dto;

import com.example.board.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class UserDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRegisterRequest {
        @NotBlank(message = "type은 필수입니다")
        private String type;

        @NotBlank(message = "email은 필수입니다")
        @Email(message = "이메일 형식에 맞지 않습니다")
        private String email;

        @NotBlank(message = "password는 필수입니다")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
        private String password;

        @NotBlank(message = "name은 필수입니다")
        private String name;

        private String image;

        private String phone;

        private String address;

        private Map<String, Object> extra;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long _id;
        private String email;
        private String name;
        private String type;
        private String image;
        private String phone;
        private String address;
        private String extra;
        private String createdAt;
        private String updatedAt;

        public static UserResponse fromEntity(User user) {
            return UserResponse.builder()
                    ._id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .type(user.getType())
                    .image(user.getImage())
                    .phone(user.getPhone())
                    .address(user.getAddress())
                    .extra(user.getExtra())
                    .createdAt(format(user.getCreatedAt()))
                    .updatedAt(format(user.getUpdatedAt()))
                    .build();
        }

        private static String format(LocalDateTime dt) {
            if (dt == null) return "";
            return dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRegisterResponse {
        private int ok;
        private UserResponse item;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserListResponse {
        private int ok;
        private List<UserResponse> item;
        private Pagination pagination;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }
}