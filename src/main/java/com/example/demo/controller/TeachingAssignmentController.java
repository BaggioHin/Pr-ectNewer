package com.example.demo.controller;


import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.request.TeachingAssignmentRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.TeachingAssignmentResponse;
import com.example.demo.service.k1.TeachingAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teaching-assignments")
public class TeachingAssignmentController {
    @Autowired
    TeachingAssignmentService teachingAssignmentService;

    @Operation(summary = "Get teaching assignment by Id")
    @GetMapping("/{id}")
    ApiResponse<TeachingAssignmentResponse> getTeachingAssignment(@PathVariable Long id){
        return ApiResponse.<TeachingAssignmentResponse>builder()
                .result(teachingAssignmentService.getTeachingAssignmentById(id))
                .build();
    }

    @Operation(summary = "Get list teaching assignments")
    @GetMapping
    ApiResponse<PageResponse<TeachingAssignmentResponse>> getListTeachingAssignment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PageResponse<TeachingAssignmentResponse>>builder()
                .result(teachingAssignmentService.getListTeachingAssignment(page, size))
                .build();
    }

    @Operation(summary = "Add teaching assignment")
    @PostMapping
    ApiResponse<TeachingAssignmentResponse> addTeachingAssignment(
            @RequestBody TeachingAssignmentRequest request){
        return ApiResponse.<TeachingAssignmentResponse>builder()
                .result(teachingAssignmentService.addTeachingAssignment(request))
                .build();
    }

    @Operation(summary = "Change status teaching assignment")
    @PutMapping("/{id}/status")
    ApiResponse<TeachingAssignmentResponse> changeStatusTeachingAssignment(
            @PathVariable Long id,
            @RequestParam String status){
        return ApiResponse.<TeachingAssignmentResponse>builder()
                .result(teachingAssignmentService.changeStatusTeachingAssignment(id, status))
                .build();
    }
}
