package com.example.demo.service.k1;

import com.example.demo.dto.request.TeachingAssignmentRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.TeachingAssignmentResponse;

public interface TeachingAssignmentService {
    TeachingAssignmentResponse getTeachingAssignmentById(Long id);

    PageResponse<TeachingAssignmentResponse> getListTeachingAssignment(int page, int size);

    TeachingAssignmentResponse addTeachingAssignment(TeachingAssignmentRequest request);

    TeachingAssignmentResponse changeStatusTeachingAssignment(Long id, String status);
}
