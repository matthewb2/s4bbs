package com.example.board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
