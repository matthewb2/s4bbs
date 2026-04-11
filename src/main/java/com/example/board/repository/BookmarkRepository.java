package com.example.board.repository;

import com.example.board.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByTypeAndUserIdAndTargetId(String type, Long userId, Long targetId);
    boolean existsByTypeAndUserIdAndTargetId(String type, Long userId, Long targetId);
    List<Bookmark> findByTypeAndUserIdOrderByCreatedAtDesc(String type, Long userId);
    List<Bookmark> findByTypeAndUserIdAndIsLikeTrueOrderByCreatedAtDesc(String type, Long userId);
}