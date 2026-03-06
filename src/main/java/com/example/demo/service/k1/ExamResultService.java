package com.example.demo.service.k1;

import com.example.demo.dto.response.ExamResultResponse;
import com.example.demo.dto.response.PageResponse;

public interface ExamResultService {
    PageResponse<ExamResultResponse> getMyResults(Long examId, int page, int size);
}
