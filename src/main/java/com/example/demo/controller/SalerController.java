package com.example.demo.controller;

import com.example.demo.dto.request.SalerRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SalerResponse;
import com.example.demo.service.k1.SalerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/saler")
public class SalerController {

    @Autowired
    private SalerService salerService;

    @GetMapping("/{id}")
    public ApiResponse<SalerResponse> getById(@PathVariable Long id) {
        return ApiResponse.<SalerResponse>builder()
                .result(salerService.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<SalerResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<SalerResponse>>builder()
                .result(salerService.getAll(page, size))
                .build();
    }

    @PostMapping
    public ApiResponse<SalerResponse> create(@Valid @RequestBody SalerRequest request) {
        return ApiResponse.<SalerResponse>builder()
                .result(salerService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<SalerResponse> update(@PathVariable Long id, @Valid @RequestBody SalerRequest request) {
        return ApiResponse.<SalerResponse>builder()
                .result(salerService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .result(salerService.delete(id))
                .build();
    }
}
