package com.example.demo.service.k1;

import com.example.demo.dto.response.AuditLogResponse;
import com.example.demo.dto.response.PageResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuditLogService {
    void logCreate(String entityType, String entityId, String createdBy);

    PageResponse<AuditLogResponse> getAudits(int page, int size);
}
