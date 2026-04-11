package com.example.board.controller;

import com.example.board.config.JwtTokenProvider;
import com.example.board.dto.BookmarkRequest;
import com.example.board.dto.BookmarkResponse;
import com.example.board.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/bookmarks", "/bookmark"})
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/{type}")
    public ResponseEntity<BookmarkResponse> add(
            @PathVariable String type,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody BookmarkRequest request
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.status(201).body(bookmarkService.addBookmark(type, request, userId));
    }

    @GetMapping("/{type}")
    public ResponseEntity<BookmarkResponse> list(
            @PathVariable String type,
            @RequestParam(value = "is_like", required = false) String isLike,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(bookmarkService.listBookmarks(type, userId, isLike));
    }

    @GetMapping("/{type}/{target_id}")
    public ResponseEntity<BookmarkResponse> get(
            @PathVariable String type,
            @PathVariable Long target_id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(bookmarkService.getBookmark(type, target_id, userId));
    }

    @DeleteMapping("/{type}/{id}")
    public ResponseEntity<BookmarkResponse> delete(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(bookmarkService.deleteBookmark(type, id, userId));
    }

    @DeleteMapping("/{_id}")
    public ResponseEntity<BookmarkResponse> deleteById(
            @PathVariable Long _id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = getUserIdFromHeader(authHeader);
        return ResponseEntity.ok(bookmarkService.deleteBookmarkById(_id, userId));
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