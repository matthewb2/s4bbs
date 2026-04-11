package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequest {
    private Long target_id;
    private Boolean is_like;
    private String memo;
    private Map<String, Object> extra;
}