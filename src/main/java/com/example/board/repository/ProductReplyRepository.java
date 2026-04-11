package com.example.board.repository;

import com.example.board.entity.ProductReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReplyRepository extends JpaRepository<ProductReply, Long> {
    List<ProductReply> findByUserId(Long userId);
    Page<ProductReply> findByUserId(Long userId, Pageable pageable);
}