package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.EnrollmentReponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    EnrollmentService enrollmentService;

    @Operation(summary = "Get enrollment by Id")
    @GetMapping("/{id}")
    ApiResponse<EnrollmentReponse> getEnrollment(@PathVariable Long id){
        return ApiResponse.<EnrollmentReponse>builder()
                .result(enrollmentService.getEnrollmentById(id))
                .build();
    }

    @Operation(summary = "Get list enrollment")
    @GetMapping
    ApiResponse<PageResponse<EnrollmentReponse>> getListEnrollment(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PageResponse<EnrollmentReponse>>builder()
                .result(enrollmentService.getListEnrollment(page,size))
                .build();
    }

    @Operation(summary = "Add enrollment")
    @PostMapping
    ApiResponse<EnrollmentReponse> addEnrollment(@RequestParam Long courseClassId,@RequestParam Long studentId){
        return ApiResponse.<EnrollmentReponse>builder()
                .result(enrollmentService.addEnrollment(courseClassId,studentId))
                .build();
    }

    @Operation(summary = "Change status")
    @PutMapping("/{id}/status")
    ApiResponse<EnrollmentReponse> changeStatusEnrollment(@PathVariable Long id,@RequestParam String status){
        return ApiResponse.<EnrollmentReponse>builder()
                .result(enrollmentService.changeStatusEnrollment(id,status))
                .build();
    }
}
