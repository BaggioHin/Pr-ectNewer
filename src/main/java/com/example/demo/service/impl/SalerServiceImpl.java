package com.example.demo.service.impl;

import com.example.demo.dto.request.SalerRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SalerResponse;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.sales.Saler;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.SalerRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.k1.SalerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalerServiceImpl implements SalerService {

    @Autowired
    private SalerRepository salerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public SalerResponse getById(Long id) {
        Saler saler = salerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(saler);
    }

    @Override
    public PageResponse<SalerResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Saler> pageResult = salerRepository.findAll(pageable);
        List<SalerResponse> data = pageResult.getContent()
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
    public SalerResponse create(SalerRequest request) {
        if (request == null || request.getUserId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Saler saler = new Saler();
        saler.setUser(user);
        applyRequest(request, saler);
        Saler saved = salerRepository.save(saler);
        return toResponse(saved);
    }

    @Override
    public SalerResponse update(Long id, SalerRequest request) {
        Saler saler = salerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        applyRequest(request, saler);
        Saler saved = salerRepository.save(saler);
        return toResponse(saved);
    }

    @Override
    public String delete(Long id) {
        Saler saler = salerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        salerRepository.delete(saler);
        return "Delete successful!";
    }

    private SalerResponse toResponse(Saler saler) {
        return SalerResponse.builder()
                .userId(saler.getUser() != null ? saler.getUser().getId() : null)
                .code(saler.getCode())
                .team(saler.getTeam())
                .build();
    }

    private void applyRequest(SalerRequest request, Saler saler) {
        if (request == null) {
            return;
        }
        if (request.getCode() != null) {
            saler.setCode(request.getCode());
        }
        if (request.getTeam() != null) {
            saler.setTeam(request.getTeam());
        }
    }
}
