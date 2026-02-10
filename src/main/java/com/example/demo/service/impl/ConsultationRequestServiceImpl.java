package com.example.demo.service.impl;

import com.example.demo.constant.ConsultationStatus;
import com.example.demo.constant.ConsultationTypeReceive;
import com.example.demo.dto.request.ConsultationRequestCreateRequest;
import com.example.demo.dto.request.ConsultationRequestStatusRequest;
import com.example.demo.dto.response.ConsultationRequestResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.sales.ConsultationRequest;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.ConsultationRequestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.k1.ConsultationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConsultationRequestServiceImpl implements ConsultationRequestService {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+?84|0)\\s*(?:\\d[\\s\\-\\.]*?){8,10}"
    );
    @Autowired
    ConsultationRequestRepository consultationRequestRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public ConsultationRequestResponse getById(Long id) {
        ConsultationRequest request = consultationRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(request);
    }

    @Override
    public PageResponse<ConsultationRequestResponse> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ConsultationRequest> pageResult = consultationRequestRepository.findAll(pageable);
        List<ConsultationRequestResponse> data = pageResult.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    public ConsultationRequestResponse add(ConsultationRequestCreateRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }

        ConsultationRequest entity = new ConsultationRequest();
        if (request.getUserId() != null) {
            var user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            entity.setUser(user);
        }
        entity.setPhone(resolvePhone(request.getPhone(), request.getMessage()));
        entity.setEmail(request.getEmail());
        entity.setMessage(request.getMessage());
        entity.setSource(request.getSource());
        entity.setTypeReceive(request.getTypeReceive() != null
                ? request.getTypeReceive()
                : ConsultationTypeReceive.MESSAGE);
        entity.setStatus(ConsultationStatus.NEW);

        ConsultationRequest saved = consultationRequestRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public ConsultationRequestResponse updateStatus(Long id, ConsultationRequestStatusRequest request) {
        ConsultationRequest entity = consultationRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (request == null || request.getStatus() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        entity.setStatus(request.getStatus());
        ConsultationRequest saved = consultationRequestRepository.save(entity);
        return toResponse(saved);
    }

    private ConsultationRequestResponse toResponse(ConsultationRequest request) {
        return ConsultationRequestResponse.builder()
                .id(request.getId())
                .userId(request.getUser() != null ? request.getUser().getId() : null)
                .phone(request.getPhone())
                .email(request.getEmail())
                .message(request.getMessage())
                .source(request.getSource())
                .status(request.getStatus())
                .typeReceive(request.getTypeReceive())
                .assignedSalerId(request.getAssignedSaler() != null ? request.getAssignedSaler().getUserId() : null)
                .closedAt(request.getClosedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    private String resolvePhone(String phone, String message) {
        if (phone != null && !phone.isBlank()) {
            return phone.trim();
        }
        if (message == null || message.isBlank()) {
            return null;
        }
        Matcher matcher = PHONE_PATTERN.matcher(message);
        if (!matcher.find()) {
            return null;
        }
        return normalizePhone(matcher.group());
    }

    private String normalizePhone(String raw) {
        String cleaned = raw.replaceAll("[^0-9+]", "");
        if (cleaned.startsWith("+84")) {
            cleaned = "0" + cleaned.substring(3);
        }
        return cleaned;
    }
}
