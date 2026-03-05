package com.example.demo.controller;

import com.example.demo.dto.request.SubjectRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SubjectResponse;
import com.example.demo.service.k1.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;
    //    Get Subject by Id
    @GetMapping("/by-id/{id}")
    ApiResponse<SubjectResponse> getSubjectById(@PathVariable Long id){
        return ApiResponse.<SubjectResponse>builder()
                .result(subjectService.getSubjectByid(id))
                .build();
    }
    //    Get Subject by name
    @GetMapping("/by-name/{name}")
    ApiResponse<List<SubjectResponse>> getSubjectByName(@PathVariable String name){
        return ApiResponse.<List<SubjectResponse>>builder()
                .result(subjectService.getSubjectByName(name))
                .build();
    }
    //    Get List Subject
    @GetMapping
    ApiResponse<PageResponse<SubjectResponse>> getSubjectByPage(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PageResponse<SubjectResponse>>builder()
                .result(subjectService.getListSubject(page, size))
                .build();
    }
    //    Get Subjects by courseId
    @GetMapping("/by-course/{courseId}")
    ApiResponse<PageResponse<SubjectResponse>> getSubjectsByCourseId(@PathVariable Long courseId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PageResponse<SubjectResponse>>builder()
                .result(subjectService.getSubjectsByCourseId(courseId, page, size))
                .build();
    }
    //    Edit Subject
    @PutMapping("/{id}")
    ApiResponse<SubjectResponse> editSubject(@PathVariable Long id,@RequestBody SubjectRequest SubjectRequest){
        return ApiResponse.<SubjectResponse>builder()
                .result(subjectService.editSubject(id,SubjectRequest))
                .build();
    }
    //    Add Subject
    @PostMapping
    ApiResponse<SubjectResponse> addSubject(@RequestBody SubjectRequest SubjectRequest){
        return ApiResponse.<SubjectResponse>builder()
                .result(subjectService.addSubject(SubjectRequest))
                .build();
    }
    //    Delete Subject
    @DeleteMapping("/{id}")
    ApiResponse<String> deleteSubject(@PathVariable Long id){
        return ApiResponse.<String>builder()
                .result(subjectService.deleteSubject(id))
                .build();
    }

}
