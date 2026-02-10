package com.example.demo.controller;

import com.example.demo.dto.request.CommentRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CommentResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{id}")
    ApiResponse<CommentResponse> getCommentById(@PathVariable Long id) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.getCommentById(id))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<CommentResponse>> getListComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long questionId) {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getListComments(page, size, questionId))
                .build();
    }

    @PostMapping
    ApiResponse<CommentResponse> addComment(@RequestBody CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.addComment(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<CommentResponse> editComment(@PathVariable Long id,
                                             @RequestBody CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.editComment(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteComment(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .result(commentService.deleteComment(id))
                .build();
    }
}
