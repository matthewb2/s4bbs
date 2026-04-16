package com.example.board.service;

import com.example.board.dto.BookmarkRequest;
import com.example.board.dto.BookmarkResponse;
import com.example.board.entity.Bookmark;
import com.example.board.entity.Post;
import com.example.board.entity.Product;
import com.example.board.entity.User;
import com.example.board.global.CustomException;
import com.example.board.global.ErrorCode;
import com.example.board.repository.BookmarkRepository;
import com.example.board.repository.PostRepository;
import com.example.board.repository.ProductRepository;
import com.example.board.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public BookmarkResponse addBookmark(String type, BookmarkRequest request, Long userId) {
        if (bookmarkRepository.existsByTypeAndUserIdAndTargetId(type, userId, request.getTarget_id())) {
            throw new CustomException(ErrorCode.DUPLICATE_BOOKMARK);
        }

        Bookmark bookmark = Bookmark.builder()
                .type(type)
                .userId(userId)
                .targetId(request.getTarget_id())
                .isLike(request.getIs_like())
                .memo(request.getMemo())
                .build();

        if (request.getExtra() != null) {
            try {
                bookmark.setExtra(objectMapper.writeValueAsString(request.getExtra()));
            } catch (JsonProcessingException e) {
                // ignore
            }
        }

        Bookmark saved = bookmarkRepository.save(bookmark);

        return BookmarkResponse.builder()
                .ok(1)
                .item(BookmarkResponse.Item.builder()
                        .type(saved.getType())
                        .user_id(userId)
                        .target_id(saved.getTargetId())
                        .memo(saved.getMemo())
                        ._id(saved.getId())
                        .createdAt(format(saved.getCreatedAt()))
                        .build())
                .build();
    }

    public BookmarkResponse listBookmarks(String type, Long userId, String isLike) {
        List<Bookmark> bookmarks;
        if ("true".equals(isLike)) {
            bookmarks = bookmarkRepository.findByTypeAndUserIdAndIsLikeTrueOrderByCreatedAtDesc(type, userId);
        } else {
            bookmarks = bookmarkRepository.findByTypeAndUserIdOrderByCreatedAtDesc(type, userId);
        }

        List<BookmarkResponse.Item> items = bookmarks.stream()
                .map(b -> mapToItem(b, type))
                .collect(Collectors.toList());

        return BookmarkResponse.builder()
                .ok(1)
                .items(items)
                .build();
    }

    public BookmarkResponse getBookmark(String type, Long targetId, Long userId) {
        Bookmark bookmark = bookmarkRepository.findByTypeAndUserIdAndTargetId(type, userId, targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOKMARK));

        BookmarkResponse.Item item = mapToItem(bookmark, type);

        return BookmarkResponse.builder()
                .ok(1)
                .item(item)
                .build();
    }

    private BookmarkResponse.Item mapToItem(Bookmark b, String type) {
        BookmarkResponse.Item.ItemBuilder builder = BookmarkResponse.Item.builder()
                .type(b.getType())
                .user_id(b.getUserId())
                .target_id(b.getTargetId())
                .memo(b.getMemo())
                ._id(b.getId())
                .createdAt(format(b.getCreatedAt()));

        switch (type) {
            case "product":
                productRepository.findById(b.getTargetId()).ifPresent(p -> builder.product(mapToProductItem(p)));
                break;
            case "user":
                userRepository.findById(b.getTargetId()).ifPresent(u -> builder.user(mapToUserItem(u)));
                break;
            case "post":
                postRepository.findById(b.getTargetId()).ifPresent(p -> builder.post(mapToPostItem(p)));
                break;
        }
        return builder.build();
    }

    private BookmarkResponse.ProductItem mapToProductItem(Product p) {
        BookmarkResponse.ImageItem image = null;
        if (p.getMainImages() != null) {
            try {
                var imgMap = objectMapper.readValue(p.getMainImages(), java.util.Map.class);
                image = BookmarkResponse.ImageItem.builder()
                        .url((String) imgMap.get("url"))
                        .name((String) imgMap.get("name"))
                        .build();
            } catch (Exception e) {
                // ignore
            }
        }
        java.util.Map<String, Object> extraMap = null;
        if (p.getExtra() != null) {
            try {
                extraMap = objectMapper.readValue(p.getExtra(), java.util.Map.class);
            } catch (Exception e) {
                // ignore
            }
        }
        return BookmarkResponse.ProductItem.builder()
                ._id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .buyQuantity(p.getBuyQuantity())
                .image(image)
                .extra(extraMap)
                .build();
    }

    private BookmarkResponse.UserItem mapToUserItem(User u) {
        return BookmarkResponse.UserItem.builder()
                ._id(u.getId())
                .name(u.getName())
                .delete(u.getDelete() != null && u.getDelete() == 1)
                .build();
    }

    private BookmarkResponse.PostItem mapToPostItem(Post p) {
        java.util.Map<String, Object> extraMap = null;
        if (p.getExtra() != null) {
            try {
                extraMap = objectMapper.readValue(p.getExtra(), java.util.Map.class);
            } catch (Exception e) {
                // ignore
            }
        }
        return BookmarkResponse.PostItem.builder()
                ._id(p.getId())
                .subject(p.getTitle())
                .content(p.getContent())
                .hit(p.getViews())
                .extra(extraMap)
                .build();
    }

    @Transactional
    public BookmarkResponse deleteBookmark(String type, Long id, Long userId) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOKMARK));

        if (!bookmark.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        bookmarkRepository.delete(bookmark);

        return BookmarkResponse.builder()
                .ok(1)
                .build();
    }

    @Transactional
    public BookmarkResponse deleteBookmarkById(Long id, Long userId) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOOKMARK));

        if (!bookmark.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        bookmarkRepository.delete(bookmark);

        return BookmarkResponse.builder()
                .ok(1)
                .build();
    }

    private String format(java.time.LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}