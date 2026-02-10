package com.example.demo.controller;

import com.example.demo.dto.request.AttendanceRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.AttendanceResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendances")
public class AttendanceController {
    @Autowired
    AttendanceService attendanceService;

    @GetMapping("/{classSessionId}/{studentId}")
    ApiResponse<AttendanceResponse> getAttendanceById(@PathVariable Long classSessionId,
                                                      @PathVariable Long studentId) {
        return ApiResponse.<AttendanceResponse>builder()
                .result(attendanceService.getAttendanceById(classSessionId, studentId))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<AttendanceResponse>> getListAttendance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<AttendanceResponse>>builder()
                .result(attendanceService.getListAttendance(page, size))
                .build();
    }

    @PostMapping
    ApiResponse<AttendanceResponse> addAttendance(@RequestBody AttendanceRequest request) {
        return ApiResponse.<AttendanceResponse>builder()
                .result(attendanceService.addAttendance(request))
                .build();
    }

    @PutMapping("/{classSessionId}/{studentId}")
    ApiResponse<AttendanceResponse> editAttendance(@PathVariable Long classSessionId,
                                                   @PathVariable Long studentId,
                                                   @RequestBody AttendanceRequest request) {
        return ApiResponse.<AttendanceResponse>builder()
                .result(attendanceService.editAttendance(classSessionId, studentId, request))
                .build();
    }

    @DeleteMapping("/{classSessionId}/{studentId}")
    ApiResponse<String> deleteAttendance(@PathVariable Long classSessionId,
                                         @PathVariable Long studentId) {
        return ApiResponse.<String>builder()
                .result(attendanceService.deleteAttendance(classSessionId, studentId))
                .build();
    }
}
