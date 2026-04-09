package com.example.board.repository;

import com.example.board.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContaining(String name, Pageable pageable);
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);
    Page<Product> findBySellerIdAndNameContaining(Long sellerId, String name, Pageable pageable);
    Page<Product> findByPriceBetween(Integer minPrice, Integer maxPrice, Pageable pageable);
    Page<Product> findByNameContainingAndPriceBetween(String name, Integer minPrice, Integer maxPrice, Pageable pageable);
}