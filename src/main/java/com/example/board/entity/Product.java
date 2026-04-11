package com.example.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private Integer price;

    private Integer shippingFees;

    @Column(name = "is_show") // 또는 "`show`" 처럼 백틱으로 감싸기
    private Boolean show;

    private Boolean active;

    private String name;

    private Integer quantity;

    private Integer buyQuantity;

    @Column(columnDefinition = "TEXT")
    private String mainImages;

    @Column(columnDefinition = "TEXT")
    private String extra;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}