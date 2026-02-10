package com.example.demo.service.k1;

import com.example.demo.dto.request.ConsultationRequestCreateRequest;
import com.example.demo.dto.request.ConsultationRequestStatusRequest;
import com.example.demo.dto.response.ConsultationRequestResponse;
import com.example.demo.dto.response.PageResponse;

public interface ConsultationRequestService {
    ConsultationRequestResponse getById(Long id);

    PageResponse<ConsultationRequestResponse> getList(int page, int size);

    ConsultationRequestResponse add(ConsultationRequestCreateRequest request);

    ConsultationRequestResponse updateStatus(Long id, ConsultationRequestStatusRequest request);
}
