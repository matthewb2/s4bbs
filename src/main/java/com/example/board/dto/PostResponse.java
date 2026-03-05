package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostResponse {

    private Long _id;
    private String type;
    private String title;
    private String content;
    private String image;
    private String tag;
    private Integer views;
}
