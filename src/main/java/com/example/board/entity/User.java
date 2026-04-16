package com.example.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String email;

    private String password;

    private String name;

    private String image;

    private String loginType;

    private String phone;

    private String address;

    @Column(name = "is_deleted") // DB 컬럼명을 delete 대신 다른 것으로 변경
    private Integer delete;

    @Column(columnDefinition = "TEXT")
    private String extra;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
