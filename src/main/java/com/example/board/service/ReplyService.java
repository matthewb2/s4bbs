package com.example.board.service;

import com.example.board.dto.ReplyRequest;
import com.example.board.dto.ReplyResponse;
import com.example.board.entity.Reply;
import com.example.board.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    @Transactional
    public ReplyResponse createReply(Long postId, ReplyRequest request, Long userId, String userName, String userImage) {
        Reply reply = Reply.builder()
                .postId(postId)
                .userId(userId)
                .userName(userName)
                .userImage(userImage)
                .content(request.getContent())
                .build();

        Reply saved = replyRepository.save(reply);

        return ReplyResponse.builder()
                .ok(1)
                .item(ReplyResponse.ReplyItem.builder()
                        .content(saved.getContent())
                        .user(ReplyResponse.ReplyUser.builder()
                                ._id(saved.getUserId())
                                .name(saved.getUserName())
                                .image(saved.getUserImage())
                                .build())
                        ._id(saved.getId())
                        .createdAt(format(saved.getCreatedAt()))
                        .updatedAt(format(saved.getUpdatedAt()))
                        .build())
                .build();
    }

    @Transactional
    public Map<String, Object> updateReply(Long replyId, ReplyRequest request, Long userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        reply.setContent(request.getContent());
        replyRepository.save(reply);

        return Map.of("ok", 1);
    }

    @Transactional
    public Map<String, Object> deleteReply(Long replyId, Long userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        replyRepository.delete(reply);
        return Map.of("ok", 1);
    }

    @Transactional(readOnly = true)
    public ReplyResponse getReplies(Long postId) {
        List<Reply> replies = replyRepository.findByPostId(postId);
        
        List<ReplyResponse.ReplyItem> items = replies.stream()
                .map(reply -> ReplyResponse.ReplyItem.builder()
                        .content(reply.getContent())
                        .user(ReplyResponse.ReplyUser.builder()
                                ._id(reply.getUserId())
                                .name(reply.getUserName())
                                .image(reply.getUserImage())
                                .build())
                        ._id(reply.getId())
                        .createdAt(format(reply.getCreatedAt()))
                        .updatedAt(format(reply.getUpdatedAt()))
                        .build())
                .collect(Collectors.toList());

        return ReplyResponse.builder()
                .ok(1)
                .itemList(items)
                .build();
    }

    private String format(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}