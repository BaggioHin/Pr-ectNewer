package com.example.demo.service.k1;

import com.example.demo.dto.response.CourseFinalScoreResponse;
import com.example.demo.dto.response.SubjectProcessScoreResponse;

import java.util.List;

public interface ScoreService {
    List<SubjectProcessScoreResponse> getSubjectProcessScores(Long courseId);

    CourseFinalScoreResponse getCourseFinalScore(Long courseId);
}
