package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ExamResultResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.ExamResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam-results")
public class ExamResultController {

    @Autowired
    private ExamResultService examResultService;

    @GetMapping("/me")
    ApiResponse<PageResponse<ExamResultResponse>> getMyResults(
            @RequestParam(required = false) Long examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ExamResultResponse>>builder()
                .result(examResultService.getMyResults(examId, page, size))
                .build();
    }
}
