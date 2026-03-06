package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CourseFinalScoreResponse;
import com.example.demo.dto.response.SubjectProcessScoreResponse;
import com.example.demo.service.k1.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/course/{courseId}/subjects")
    ApiResponse<List<SubjectProcessScoreResponse>> getSubjectProcessScores(
            @PathVariable Long courseId) {
        return ApiResponse.<List<SubjectProcessScoreResponse>>builder()
                .result(scoreService.getSubjectProcessScores(courseId))
                .build();
    }

    @GetMapping("/course/{courseId}/final")
    ApiResponse<CourseFinalScoreResponse> getCourseFinalScore(
            @PathVariable Long courseId) {
        return ApiResponse.<CourseFinalScoreResponse>builder()
                .result(scoreService.getCourseFinalScore(courseId))
                .build();
    }
}
