package com.example.board.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {

    private String type;
    private String title;
    private String content;
    private String image;
    private String tag;
    private Long product_id;
}
