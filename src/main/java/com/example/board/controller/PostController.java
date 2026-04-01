package com.example.board.controller;

import com.example.board.dto.*;
import com.example.board.entity.Post;
import com.example.board.service.FtpService;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FtpService ftpService;

    // 65라인 근처: 이 메서드가 클래스 중괄호 안에 있어야 합니다!
    @GetMapping("")
    public ResponseEntity<PostListResponse> getPosts(
            @RequestHeader(value = "client-id", required = false) String clientId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(postService.findAllPosts(type, keyword, pageable, clientId));
    }

    @PostMapping
    public PostCreateResponse create(
            @RequestHeader(value = "client-id", required = false) String clientId,
            @RequestBody PostCreateRequest dto
    ) {

        System.out.println(dto);

        return postService.create(dto, clientId);
    }
    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPostWithImage(
            @RequestHeader(value = "client-id", required = false) String clientId,
            @RequestPart("post") PostRequest postRequest, // 게시글 내용 (JSON)
            @RequestPart(value = "file", required = false) MultipartFile file // 이미지 파일
    ) {
        String savedFileName = null;

        // 1. 파일이 있으면 FTP 업로드 수행
        if (file != null && !file.isEmpty()) {
            savedFileName = ftpService.uploadFile(file);
        }

        // 2. 게시글 정보와 파일명을 매핑하여 저장
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .image(savedFileName) // 저장된 UUID 파일명 기록
                .userName(postRequest.getUserName())
                .clientId(clientId)
                .build();

        Post savedPost = postService.save(post);
        return ResponseEntity.ok(savedPost);
    }
}
