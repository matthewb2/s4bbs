package com.example.board.controller;

import com.example.board.dto.PostCreateRequest;
import com.example.board.dto.PostCreateResponse;
import com.example.board.dto.PostResponse;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostCreateResponse create(
            @RequestBody PostCreateRequest dto
    ) {

        System.out.println(dto);

        return postService.create(dto);
    }
    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable Long id) {
        return postService.findById(id);
    }

}
