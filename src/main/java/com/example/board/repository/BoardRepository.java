package com.example.board.repository;

import com.example.board.entity.Post;
import com.example.board.entity.Post;
import org.springframework.data.domain.Page;         // 추가
import org.springframework.data.domain.Pageable;     // 추가
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Post, Long> {


}