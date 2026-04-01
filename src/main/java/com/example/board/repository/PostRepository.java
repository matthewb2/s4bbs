package com.example.board.repository;

import com.example.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 1. 타입별 조회 (페이징 지원)
    Page<Post> findByType(String type, Pageable pageable);

    // 2. 타입 + 키워드(제목 포함) 조회 (페이징 지원) - 에러 발생 지점 해결
    Page<Post> findByTypeAndTitleContaining(String type, String title, Pageable pageable);

    // (선택) 만약 제목뿐만 아니라 내용까지 검색하고 싶다면 아래와 같이 작성할 수도 있습니다.
    // Page<Post> findByTypeAndTitleContainingOrContentContaining(String type, String title, String content, Pageable pageable);

    Page<Post> findByClientId(String clientId, Pageable pageable);

    Page<Post> findByClientIdAndType(String clientId, String type, Pageable pageable);

    Page<Post> findByClientIdAndTypeAndTitleContaining(String clientId, String type, String title, Pageable pageable);
}