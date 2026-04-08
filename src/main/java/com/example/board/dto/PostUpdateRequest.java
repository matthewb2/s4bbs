package com.example.board.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
public class PostUpdateRequest {
    private String title;
    private String content;
    private String image;
    private Map<String, Object> extra;
}