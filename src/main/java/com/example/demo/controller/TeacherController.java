package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.TeacherResponse;
import com.example.demo.service.k1.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    @Autowired
    TeacherService teacherService;

    @GetMapping("/by-course-class/{courseClassId}")
    ApiResponse<List<TeacherResponse>> getTeacherByCourseClass(@PathVariable Long courseClassId){
        return ApiResponse.<List<TeacherResponse>>builder()
                .result(teacherService.getTeacherByCourseClass(courseClassId))
                .build();
    }
}
