package com.example.board.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import com.example.board.dto.*;
import com.example.board.entity.Post;
import com.example.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // 이 줄이 핵심입니다.
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    @Value("${ftp.server}")
    private String imageServer;

    private String imageBaseUrl;
    @PostConstruct
    public void init() {
        // 모든 주입이 끝난 후 실행되므로 imageServer가 null이 아닙니다.
        this.imageBaseUrl = imageServer + "/images/";
    }



    public PostCreateResponse create(PostCreateRequest req, String clientId) {


        LocalDateTime now = LocalDateTime.now();

        Post post = Post.builder()
                .type(req.getType() == null ? "post" : req.getType())
                .title(req.getTitle())
                .content(req.getContent())
                .image(req.getImage())
                .tag(req.getTag())
                .productId(req.getProduct_id())
                .views(0)
                .userId(4L)              // ★ 인증 붙으면 교체
                .userName("제이지")       // ★ 인증 붙으면 교체
                .clientId(clientId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Post saved = postRepository.save(post);

        return PostCreateResponse.builder()
                .ok(1)
                .item(
                        PostCreateResponse.Item.builder()
                                .type(saved.getType())
                                .title(saved.getTitle())
                                .content(saved.getContent())
                                .image(saved.getImage())
                                .tag(saved.getTag())
                                .views(saved.getViews())
                                .user(
                                        PostCreateResponse.User.builder()
                                                ._id(saved.getUserId())
                                                .name(saved.getUserName())
                                                .build()
                                )
                                ._id(saved.getId())
                                .createdAt(format(saved.getCreatedAt()))
                                .updatedAt(format(saved.getUpdatedAt()))
                                .build()
                )
                .build();
    }

    private String format(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
    public PostResponse findById(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return PostResponse.builder()
                ._id(post.getId())
                .type(post.getType())
                .title(post.getTitle())
                .content(post.getContent())
                .image(post.getImage() != null ? imageBaseUrl + post.getImage() : null)
                //.image(post.getImage() != null ? "https://mksolution.dothome.co.kr/images/" + post.getImage() : null)
                .tag(post.getTag())
                .views(post.getViews())
                .build();
    }

    // 이 메서드가 추가되어야 합니다.
    public Post save(Post post) {
        return postRepository.save(post);
    }

    public PostListResponse findAllPosts(String type, String keyword, Pageable pageable, String clientId) {
        Page<Post> postPage;
        if (clientId != null && !clientId.isEmpty()) {
            if (type != null && keyword != null) {
                postPage = postRepository.findByClientIdAndTypeAndTitleContaining(clientId, type, keyword, pageable);
            } else if (type != null) {
                postPage = postRepository.findByClientIdAndType(clientId, type, pageable);
            } else {
                postPage = postRepository.findByClientId(clientId, pageable);
            }
        } else {
            if (type != null && keyword != null) {
                postPage = postRepository.findByTypeAndTitleContaining(type, keyword, pageable);
            } else if (type != null) {
                postPage = postRepository.findByType(type, pageable);
            } else {
                postPage = postRepository.findAll(pageable);
            }
        }
// PostService.java 내의 해당 부분 수정
        List<PostItem> items = postPage.getContent().stream()
                .map(post -> {
                    // [수정] 날짜 포맷팅 및 Null 체크 안전하게 처리
                    String formattedDate = "";
                    if (post.getCreatedAt() != null) {
                        // 명세서 규격에 맞게 포맷팅 (예: 2026.01.14 18:14:07)
                        formattedDate = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
                    }

                    return PostItem.builder()
                            ._id(post.getId())
                            .type(post.getType())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .image(post.getImage() != null ? imageBaseUrl + post.getImage() : null)
                            //.image(post.getImage() != null ? "https://mksolution.dothome.co.kr/images/" + post.getImage() : null)
                            .createdAt(formattedDate) // 안전하게 처리된 날짜 사용
                            .updatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : "")
                            .user(Map.of("_id", 1, "name", post.getUserName() != null ? post.getUserName() : "익명"))
                            .build();
                })
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .ok(1)
                .item(items)
                .pagination(PostListResponse.Pagination.builder()
                        .page(pageable.getPageNumber() + 1)
                        .limit(pageable.getPageSize())
                        .total(postPage.getTotalElements())
                        .totalPages(postPage.getTotalPages())
                        .build())
                .build();
    }

}
