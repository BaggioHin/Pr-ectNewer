package com.example.demo.service.k1;

import com.example.demo.dto.request.CommentRequest;
import com.example.demo.dto.response.CommentResponse;
import com.example.demo.dto.response.PageResponse;

public interface CommentService {
    CommentResponse getCommentById(Long id);

    PageResponse<CommentResponse> getListComments(int page, int size, Long questionId);

    CommentResponse addComment(CommentRequest request);

    CommentResponse editComment(Long id, CommentRequest request);

    String deleteComment(Long id);
}
