package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.StudentResponse;
import com.example.demo.service.k1.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/Student")
public class StudentController {
    @Autowired
    StudentService studentService;

    @GetMapping("/by-course-class/{courseClassId}")
    ApiResponse<List<StudentResponse>> getStudentByCourseClass(@PathVariable Long courseClassId){
        return ApiResponse.<List<StudentResponse>>builder()
                .result(studentService.getStudentByCourseClass(courseClassId))
                .build();
    }

}
