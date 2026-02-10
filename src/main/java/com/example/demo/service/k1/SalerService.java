package com.example.demo.service.k1;

import com.example.demo.dto.request.SalerRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SalerResponse;

public interface SalerService {
    SalerResponse getById(Long id);

    PageResponse<SalerResponse> getAll(int page, int size);

    SalerResponse create(SalerRequest request);

    SalerResponse update(Long id, SalerRequest request);

    String delete(Long id);
}
