package com.example.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.board.dto.*;
import com.example.board.entity.Post;
import com.example.board.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;
    @Value("${ftp.server}")
    private String imageServer;

    private String imageBaseUrl;

    public PostService(PostRepository postRepository, ObjectMapper objectMapper) {
        this.postRepository = postRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        this.imageBaseUrl = imageServer + "/images/";
    }

    public PostCreateResponse create(PostCreateRequest req, String clientId, Long userId, String userName) {
        LocalDateTime now = LocalDateTime.now();

        Map<String, Object> extra = req.getExtra();
        if (extra == null) {
            extra = new HashMap<>();
        }
        extra.put("user", Map.of("_id", userId, "name", userName));

        Post post = Post.builder()
                .type(req.getType() == null ? "post" : req.getType())
                .title(req.getTitle())
                .content(req.getContent())
                .image(req.getImage())
                .tag(req.getTag())
                .productId(req.getProduct_id())
                .views(0)
                .userId(userId)
                .userName(userName)
                .clientId(clientId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        try {
            post.setExtra(objectMapper.writeValueAsString(extra));
        } catch (JsonProcessingException e) {
            // ignore
        }

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
                .tag(post.getTag())
                .views(post.getViews())
                .build();
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getImage() != null) {
            post.setImage(request.getImage());
        }
        if (request.getTag() != null) {
            post.setTag(request.getTag());
        }

        Post saved = postRepository.save(post);

        return PostUpdateResponse.builder()
                .ok(1)
                .item(PostUpdateResponse.PostUpdateItem.builder()
                        ._id(saved.getId())
                        .title(saved.getTitle())
                        .content(saved.getContent())
                        .updatedAt(format(saved.getUpdatedAt()))
                        .build())
                .build();
    }

    public Map<String, Object> deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        postRepository.delete(post);
        return Map.of("ok", 1);
    }

    public PostListResponse findMyPosts(Long userId, String type, String keyword, Pageable pageable) {
        Page<Post> postPage;

        if (type != null && keyword != null) {
            postPage = postRepository.findByUserIdAndTypeAndTitleContaining(userId, type, keyword, pageable);
        } else if (type != null) {
            postPage = postRepository.findByUserIdAndType(userId, type, pageable);
        } else {
            postPage = postRepository.findByUserId(userId, pageable);
        }

        List<PostItem> items = postPage.getContent().stream()
                .map(post -> {
                    String formattedDate = "";
                    if (post.getCreatedAt() != null) {
                        formattedDate = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
                    }

                    return PostItem.builder()
                            ._id(post.getId())
                            .type(post.getType())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .image(post.getImage() != null ? imageBaseUrl + post.getImage() : null)
                            .createdAt(formattedDate)
                            .updatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : "")
                            .user(PostItem.PostUser.builder()
                                    ._id(post.getUserId() != null ? post.getUserId() : 1L)
                                    .name(post.getUserName() != null ? post.getUserName() : "익명")
                                    .build())
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

        List<PostItem> items = postPage.getContent().stream()
                .map(post -> {
                    String formattedDate = "";
                    if (post.getCreatedAt() != null) {
                        formattedDate = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
                    }

                    return PostItem.builder()
                            ._id(post.getId())
                            .type(post.getType())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .image(post.getImage() != null ? imageBaseUrl + post.getImage() : null)
                            .createdAt(formattedDate)
                            .updatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : "")
                            .user(PostItem.PostUser.builder()
                                    ._id(post.getUserId() != null ? post.getUserId() : 1L)
                                    .name(post.getUserName() != null ? post.getUserName() : "익명")
                                    .build())
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
