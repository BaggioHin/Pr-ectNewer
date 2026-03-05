package com.example.demo.controller;

import com.example.demo.dto.request.CourseClassRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CourseClassResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.CourseClassService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-classes")
public class CourseClassController {

    @Autowired
    private CourseClassService courseClassService;

    @Operation(summary = "Get CourseClass by Id")
    @GetMapping("/me")
    ApiResponse<CourseClassResponse> getCourseClassById(){
        return ApiResponse.<CourseClassResponse>builder()
                .result(courseClassService.getCourseClassById())
                .build();
    }


    @Operation(summary = "Get CourseClass by name")
    @GetMapping("/search")
    ApiResponse<List<CourseClassResponse>> getCourseClassByName(@RequestParam String name){
        return ApiResponse.<List<CourseClassResponse>>builder()
                .result(courseClassService.getCourseClassByName(name))
                .build();
    }


    @Operation(summary = "Get List CourseClass")
    @GetMapping
    ApiResponse<PageResponse<CourseClassResponse>> getCourseClassByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PageResponse<CourseClassResponse>>builder()
                .result(courseClassService.getListCourseClass(page,size))
                .build();
    }

    @Operation(summary = "Get CourseClass by courseId")
    @GetMapping("/by-course/{courseId}")
    ApiResponse<PageResponse<CourseClassResponse>> getCourseClassByCourseId(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PageResponse<CourseClassResponse>>builder()
                .result(courseClassService.getCourseClassesByCourseId(courseId, page, size))
                .build();
    }


    @Operation(summary = "Edit CourseClass")
    @PutMapping("/{id}")
    ApiResponse<CourseClassResponse> editCourseClass(
            @PathVariable Long id,
            @Valid @RequestBody CourseClassRequest courseClassRequest){
        return ApiResponse.<CourseClassResponse>builder()
                .result(courseClassService.editCourseClass(id,courseClassRequest))
                .build();
    }


    @Operation(summary = "Add CourseClass")
    @PostMapping
    ApiResponse<CourseClassResponse> addCourseClass(
            @Valid @RequestBody CourseClassRequest courseClassRequest){
        return ApiResponse.<CourseClassResponse>builder()
                .result(courseClassService.addCourseClass(courseClassRequest))
                .build();
    }


    @Operation(summary = "Delete CourseClass by Id")
    @DeleteMapping("/{id}")
    ApiResponse<String> deleteCourseClass(@PathVariable Long id){
        return ApiResponse.<String>builder()
                .result(courseClassService.deleteCourseClass(id))
                .build();
    }

}
