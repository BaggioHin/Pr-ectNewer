package com.example.demo.service.impl;

import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SalerStudentTransactionResponse;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.SalerRepository;
import com.example.demo.repository.SalerTransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.k1.SalerTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalerTransactionServiceImpl implements SalerTransactionService {

    @Autowired
    SalerTransactionRepository salerTransactionRepository;
    @Autowired
    SalerRepository salerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CourseClassRepository courseClassRepository;

    @Override
    @PreAuthorize("hasAnyRole('SALER','ADMIN')")
    public PageResponse<SalerStudentTransactionResponse> getStudentTransactions(Long userId, int page, int size) {
        if (userId == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        var resolvedSaler = salerRepository.findByUser_Id(userId);
        if (resolvedSaler == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        ensureSalerAccess(userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<com.example.demo.entity.sales.SalerTransaction> pageResult =
                salerTransactionRepository.findDistinctLatestBySalerId(resolvedSaler.getId(), pageable);
        var transactions = pageResult.getContent();
        if (transactions.isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        Map<Long, User> userCache = new LinkedHashMap<>();
        Map<Long, CourseClass> courseClassCache = new LinkedHashMap<>();
        List<SalerStudentTransactionResponse> data = new java.util.ArrayList<>();
        for (var t : transactions) {
            String studentName = t.getStudentName();
            String courseName = t.getCourseName();
            String studentEmail = null;
            String studentPhone = null;
            Long studentId = t.getStudentId();
            if (studentId != null) {
                User user = userCache.computeIfAbsent(studentId,
                        id -> userRepository.findById(id).orElse(null));
                if (user != null) {
                    studentEmail = user.getEmail();
                    studentPhone = user.getPhone();
                }
            }
            String courseClassName = null;
            Long courseClassId = t.getCourseClassId();
            if (courseClassId != null) {
                CourseClass courseClass = courseClassCache.computeIfAbsent(courseClassId,
                        id -> courseClassRepository.findById(id).orElse(null));
                if (courseClass != null) {
                    courseClassName = courseClass.getName();
                }
            }
            data.add(SalerStudentTransactionResponse.builder()
                    .studentName(studentName)
                    .studentEmail(studentEmail)
                    .studentPhone(studentPhone)
                    .courseName(courseName)
                    .courseClassName(courseClassName)
                    .occurredAt(t.getOccurredAt())
                    .build());
        }
        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    private void ensureSalerAccess(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return;
        }
        boolean isSaler = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SALER".equals(a.getAuthority()));
        if (!isSaler) {
            return;
        }
        Long currentUserId = jwt.getClaim("userId");
        if (currentUserId == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var currentSaler = salerRepository.findByUser_Id(currentUserId);
        if (currentSaler == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        if (!currentUserId.equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
