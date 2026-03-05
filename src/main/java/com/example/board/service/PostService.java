package com.example.board.service;

import com.example.board.dto.PostCreateRequest;
import com.example.board.dto.PostCreateResponse;
import com.example.board.dto.PostResponse;
import com.example.board.entity.Post;
import com.example.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    public PostCreateResponse create(PostCreateRequest req) {


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
                .image(post.getImage())
                .tag(post.getTag())
                .views(post.getViews())
                .build();
    }

}
