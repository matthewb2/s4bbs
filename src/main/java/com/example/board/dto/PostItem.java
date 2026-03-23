package com.example.board.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Map;


@Data
@Builder
public class PostItem {
    private Long _id; // 명세서의 ID 필드명 대응
    private String type;
    private String title;
    private String content;
    private String image;
    private String createdAt;
    private String updatedAt;

    // 추가 정보 (필요 시 연관관계 매핑)
    private Map<String, Object> user;
    private Integer repliesCount;
}
