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

    private String format(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}