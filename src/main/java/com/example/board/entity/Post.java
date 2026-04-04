package com.example.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post", indexes = {
        @Index(name = "idx_post_client_created", columnList = "clientId, createdAt DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;        // default post
    private String title;
    private String content;
    private String image;
    private String tag;
    private Long productId;

    private Integer views;

    private Long userId;
    private String userName;
    private String clientId;

    @CreationTimestamp // 데이터 생성 시 현재 시간 자동 삽입
    private LocalDateTime createdAt;

    @UpdateTimestamp // 데이터 수정 시 현재 시간 자동 갱신
    private LocalDateTime updatedAt;
}
