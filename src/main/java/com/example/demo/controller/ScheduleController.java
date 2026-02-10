package com.example.demo.controller;

import com.example.demo.dto.request.ScheduleRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ScheduleResponse;
import com.example.demo.service.k1.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;


    //    Add Schedule in CourseClass
    @PostMapping("/addSchedule")
    ApiResponse<ScheduleResponse> addScheduleInCourseClass(@RequestBody ScheduleRequest request){
        return ApiResponse.<ScheduleResponse>builder()
                .result(scheduleService.addScheduleInCourseClass(request))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ScheduleResponse> getScheduleById(@PathVariable Long id) {
        return ApiResponse.<ScheduleResponse>builder()
                .result(scheduleService.getScheduleById(id))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<ScheduleResponse>> getListSchedule(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ScheduleResponse>>builder()
                .result(scheduleService.getListSchedule(page, size))
                .build();
    }

    //    Delete Schudule in CourseClass
    @DeleteMapping("/deleteSchedule/{id}")
    ApiResponse<String> deleteScheduleInCourseClass(@PathVariable Long id){
        return ApiResponse.<String>builder()
                .result(scheduleService.deleteScheduleInCourseClass(id))
                .build();
    }
}
