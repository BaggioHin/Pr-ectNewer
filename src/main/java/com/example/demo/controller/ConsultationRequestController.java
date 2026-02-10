package com.example.demo.controller;

import com.example.demo.dto.request.ConsultationRequestCreateRequest;
import com.example.demo.dto.request.ConsultationRequestStatusRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ConsultationRequestResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.k1.ConsultationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consultation-requests")
public class ConsultationRequestController {
    @Autowired
    ConsultationRequestService consultationRequestService;

    @GetMapping("/{id}")
    ApiResponse<ConsultationRequestResponse> getById(@PathVariable Long id) {
        return ApiResponse.<ConsultationRequestResponse>builder()
                .result(consultationRequestService.getById(id))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<ConsultationRequestResponse>> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ConsultationRequestResponse>>builder()
                .result(consultationRequestService.getList(page, size))
                .build();
    }

    @PostMapping
    ApiResponse<ConsultationRequestResponse> add(@RequestBody ConsultationRequestCreateRequest request) {
        return ApiResponse.<ConsultationRequestResponse>builder()
                .result(consultationRequestService.add(request))
                .build();
    }

    @PutMapping("/{id}/status")
    ApiResponse<ConsultationRequestResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody ConsultationRequestStatusRequest request) {
        return ApiResponse.<ConsultationRequestResponse>builder()
                .result(consultationRequestService.updateStatus(id, request))
                .build();
    }
}
