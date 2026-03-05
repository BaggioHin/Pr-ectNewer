package com.example.demo.service.impl;

import com.example.demo.dto.response.AuditLogResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.audit.AuditLog;
import com.example.demo.entity.authAndUser.Role;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.k1.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void logCreate(String entityType, String entityId, String createdBy) {
//        String resolvedCreatedBy = createdBy != null ? createdBy : resolveCurrentUserCode();
        String resolvedEntityType = entityType != null ? entityType : resolveCurrentUserRoleName();
        System.out.println("-------------------------------------------------------------------------");
        AuditLog log = AuditLog.builder()
                .action("CREATE")
                .entityType(resolvedEntityType)
                .entityId(entityId)
                .contentUser(entityId)
//                .titleExam()
                .createdBy(createdBy)
                .build();
        auditLogRepository.save(log);
    }

    @Override
    public PageResponse<AuditLogResponse> getAudits(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AuditLog> pageResult = auditLogRepository.findAll(pageable);
        List<AuditLogResponse> data = pageResult.getContent().stream()
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

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .createdBy(log.getCreatedBy())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private Long resolveCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
                return null;
            }
            return jwt.getClaim("userId");
        } catch (Exception ignored) {
            return null;
        }
    }

    private String resolveCurrentUserCode() {
        Long userId = resolveCurrentUserId();
        if (userId == null) {
            return null;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        boolean isStudent = user.getRoles().stream().map(Role::getName).anyMatch("STUDENT"::equals);
        boolean isTeacher = user.getRoles().stream().map(Role::getName).anyMatch("TEACHER"::equals);
        boolean isSaler = user.getRoles().stream().map(Role::getName).anyMatch("SALER"::equals);
        if (isStudent && user.getStudent() != null) {
            return user.getStudent().getStudentCode();
        }
        if (isTeacher && user.getTeacher() != null) {
            return user.getTeacher().getTeacherCode();
        }
        if (isSaler && user.getSale() != null) {
            return user.getSale().getCode();
        }
        return user.getUsername();
    }

    private String resolveCurrentUserRoleName() {
        Long userId = resolveCurrentUserId();
        if (userId == null) {
            return null;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        boolean isAdmin = user.getRoles().stream().map(Role::getName).anyMatch("ADMIN"::equals);
        if (isAdmin) {
            return "ADMIN";
        }
        boolean isStudent = user.getRoles().stream().map(Role::getName).anyMatch("STUDENT"::equals);
        if (isStudent) {
            return "STUDENT";
        }
        boolean isTeacher = user.getRoles().stream().map(Role::getName).anyMatch("TEACHER"::equals);
        if (isTeacher) {
            return "TEACHER";
        }
        boolean isSaler = user.getRoles().stream().map(Role::getName).anyMatch("SALER"::equals);
        if (isSaler) {
            return "SALER";
        }
        return null;
    }
}
