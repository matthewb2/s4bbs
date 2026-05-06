package com.example.board.controller;

import com.example.board.config.JwtTokenProvider;
import com.example.board.dto.*;
import com.example.board.entity.Post;
import com.example.board.repository.UserRepository;
import com.example.board.service.FtpService;
import com.example.board.service.PostService;
import com.example.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FtpService ftpService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ReplyService replyService;
    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<PostListResponse> getMyPosts(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        int pageNum = Math.max(0, page - 1);
        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNum, limit, sortObj);
        
        return ResponseEntity.ok(postService.findMyPosts(userId, type, keyword, pageable));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<PostListResponse> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        int pageNum = Math.max(0, page - 1);
        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNum, limit, sortObj);
        
        return ResponseEntity.ok(postService.findMyPosts(userId, type, keyword, pageable));
    }

    // 65라인 근처: 이 메서드가 클래스 중괄호 안에 있어야 합니다!
    @GetMapping("/")
    public ResponseEntity<PostListResponse> getPosts(
            @RequestHeader(value = "client-id", required = false) String clientId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        // page가 1 미만으로 들어올 경우를 대비해 Math.max 사용
        int pageNum = Math.max(0, page - 1);

        // [수정] 정렬 조건을 명확히 전달
        Pageable pageable = PageRequest.of(pageNum, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(postService.findAllPosts(type, keyword, pageable, clientId));
    }

    @PostMapping("/")
    public PostCreateResponse create(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "client-id", required = false) String clientId,
            @RequestBody PostCreateRequest dto
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        String userName = null;
        var user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            userName = user.getName();
        }
        return postService.create(dto, clientId, userId, userName);
    }

    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostUpdateResponse> updatePost(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody PostUpdateRequest request
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(postService.updatePost(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(postService.deletePost(id, userId));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<ReplyResponse> getReplies(@PathVariable Long id) {
        return ResponseEntity.ok(replyService.getReplies(id));
    }

    @PostMapping("/{id}/replies")
    public ResponseEntity<ReplyResponse> createReply(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ReplyRequest request
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        var user = userRepository.findById(userId).orElse(null);
        String userName = request.getName();
        String userImage = null;
        if (user != null) {
            userName = user.getName();
            userImage = user.getImage();
        }
        return ResponseEntity.status(201).body(replyService.createReply(id, request, userId, userName, userImage));
    }

    @PatchMapping("/{id}/replies/{replyId}")
    public ResponseEntity<Map<String, Object>> updateReply(
            @PathVariable Long id,
            @PathVariable Long replyId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ReplyRequest request
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(replyService.updateReply(replyId, request, userId));
    }

    @DeleteMapping("/{id}/replies/{replyId}")
    public ResponseEntity<Map<String, Object>> deleteReply(
            @PathVariable Long id,
            @PathVariable Long replyId,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(replyService.deleteReply(replyId, userId));
    }

    private Long getUserIdFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getUserIdFromToken(token);
            }
        }
        return 4L;
    }
}
