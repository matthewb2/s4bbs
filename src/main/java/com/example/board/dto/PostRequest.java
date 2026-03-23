package com.example.board.dto; // 본인의 패키지 경로에 맞게 수정

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private String title;
    private String content;
    private String userName;
    private String type;
    private String tag;
    private Long productId;
}