package com.example.demo.controller;

import com.example.demo.dto.request.CourseRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CourseResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    //    Get Course by Id
    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> getById(@PathVariable Long id) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.getCourseById(id))
                .build();
    }

    //    Get Course by name
    @GetMapping("/search")
    public ApiResponse<CourseResponse> getByName(@RequestParam String name) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.getCourseByName(name))
                .build();
    }

    //    Get List Course
    @GetMapping
    public ApiResponse<PageResponse<CourseResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<CourseResponse>>builder()
                .result(courseService.getListCourse(page, size))
                .build();
    }

    //    Edit Course
    @PutMapping("/{id}")
    ApiResponse<CourseResponse> editCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest courseRequest){
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.editCourse(id, courseRequest))
                .build();
    }
    //    Add Course
    @PostMapping
    ApiResponse<CourseResponse> addCourse(@Valid @RequestBody CourseRequest courseRequest){
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.addCourse(courseRequest))
                .build();
    }
    //    Delete Course
    @DeleteMapping("/{id}")
    ApiResponse<String> deleteCourse(@PathVariable Long id){
        return ApiResponse.<String>builder()
                .result(courseService.deleteCourse(id))
                .build();
    }

//    // Add CourseClass in course
//    @PostMapping("/{id}/classes")
//    ApiResponse<String> addCourseClassInCourse(@PathVariable Long id, @RequestParam Long classId){
//        return ApiResponse.<String>builder()
//                .result(courseService.addCourseClass(id, classId))
//                .build();
//    }
//
//    // Delete CourseClass in course
//    @DeleteMapping("/{id}/classes/{classId}")
//    ApiResponse<String> deleteCourseClassInCourse(@PathVariable Long id, @PathVariable Long classId){
//        return ApiResponse.<String>builder()
//                .result(courseService.deleteCourseClass(id, classId))
//                .build();
//    }
//
//    //    Add subject in course
//    @PostMapping("/{id}/subjects")
//    ApiResponse<String> addSubjectInCourse(@PathVariable Long id, @RequestParam Long subjectId){
//        return ApiResponse.<String>builder()
//                .result(courseService.addSubject(id, subjectId))
//                .build();
//    }
//
//    //    Delete subject in course
//    @DeleteMapping("/{id}/subjects/{subjectId}")
//    ApiResponse<String> deleteSubjectInCourse(@PathVariable Long id, @PathVariable Long subjectId){
//        return ApiResponse.<String>builder()
//                .result(courseService.deleteSubject(id, subjectId))
//                .build();
//    }
}
