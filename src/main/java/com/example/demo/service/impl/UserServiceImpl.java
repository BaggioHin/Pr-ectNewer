package com.example.demo.service.impl;

import com.example.demo.constant.CreateType;
import com.example.demo.constant.StatusUser;
import com.example.demo.dto.request.CloudinaryUploadResult;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.UserMeResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.authAndUser.Role;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.authAndUser.UserProfile;
import com.example.demo.entity.people.Student;
import com.example.demo.entity.people.Teacher;
import com.example.demo.entity.sales.Saler;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserProfileMapper;
import com.example.demo.repository.*;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.k1.AuditLogService;
import com.example.demo.service.k1.AdminStatsService;
import com.example.demo.service.k1.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserProfileMapper userProfileMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    SalerRepository salerRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    AuditLogService auditLogService;
    @Autowired
    AdminStatsService adminStatsService;

    @Override
    @PreAuthorize("isAuthenticated() and (#id == authentication.principal.claims['userId'] or hasAuthority('ADMIN'))")
    public UserResponse getUserByid(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UserProfile profile = user.getProfile();
        if (profile == null) {
            throw new AppException(ErrorCode.USERPROFILE_NOT_EXISTED);
        }
        UserResponse response = userMapper.userToUserResponse(profile);
        response.setId(resolveUserCode(user));
        return response;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','SALER')")
    public List<UserResponse> getUserByName(String name) {
        List<UserProfile> userProfile = userProfileRepository.findByName(name);
        List<UserResponse> responses = userMapper.userToUserResponses(userProfile);
        for (int i = 0; i < responses.size(); i++) {
            UserProfile profile = userProfile.get(i);
            UserResponse response = responses.get(i);
            response.setId(resolveUserCode(profile.getUser()));
        }
        return responses;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUserByRole(CreateType role) {
        if (role == null) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
        List<UserProfile> userProfiles = userProfileRepository.findByRoleName(role.name());
        List<UserResponse> responses = userMapper.userToUserResponses(userProfiles);
        for (int i = 0; i < responses.size(); i++) {
            UserProfile profile = userProfiles.get(i);
            UserResponse response = responses.get(i);
            String code = resolveUserCodeByRole(profile, role);
            response.setId(code);
        }
        return responses;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getListUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<UserProfile> pageResult = userProfileRepository.findAll(pageable);

        List<UserResponse> data =
                userMapper.userToUserResponses(pageResult.getContent());
        List<UserProfile> profiles = pageResult.getContent();
        for (int i = 0; i < data.size(); i++) {
            UserProfile profile = profiles.get(i);
            UserResponse response = data.get(i);
            response.setId(resolveUserCode(profile.getUser()));
        }

        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public UserMeResponse getMyInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long userId = jwt.getClaim("userId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserMeResponse.builder()
                .username(user.getUsername())
                .roles(roles)
                .build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public String editUser(UserRequest userRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long userId = jwt.getClaim("userId");

        if (userRequest.getUsername() != null &&
                userRepository.existsByUsernameAndIdNot(userRequest.getUsername(), userId)) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        if (userRequest.getEmail() != null && userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userRequest.getPhone() != null && userRepository.existsByPhone(userRequest.getPhone())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserProfile userProfile = user.getProfile();

        userMapper.requestToUser(userRequest, user);
        userProfileMapper.requestToUserProfile(userRequest, userProfile);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        userProfileRepository.save(userProfile);
        return "Edit successful!";
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','SALER')")
    public Long addUser(UserRequest userRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long creatorId = jwt.getClaim("userId");
        String username = auth.getName();

        if (userRequest.getUsername() == null || userRequest.getUsername().isBlank()) {
            throw new AppException(ErrorCode.USERNAME_INVALID);
        }

        if (userRequest.getPassword() == null || userRequest.getPassword().isBlank()) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        System.out.println("ROLE:"+userRequest.getRole());
        if (userRequest.getRole() == null) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        if (userRequest.getEmail() != null && userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userRequest.getPhone() != null && userRepository.existsByPhone(userRequest.getPhone())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        User user = new User();
        UserProfile userProfile = new UserProfile();

        userMapper.requestToUser(userRequest, user);
        userProfileMapper.requestToUserProfile(userRequest, userProfile);

        user.setProfile(userProfile);
        userProfile.setName(userRequest.getName());
        userProfile.setUser(user);

        user.setStatus(StatusUser.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        String roleName = String.valueOf(userRequest.getRole());
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STATUS));
        user.getRoles().add(role);
        user.setCreatedBy(creatorId);

        Saler saler = null;
        if (userRequest.getRole() == CreateType.STUDENT) {
            Student student = new Student();
            student.setUser(user);
            student.setStudentCode(generateStudentCode());
            user.setStudent(student);
        }
        if (userRequest.getRole() == CreateType.TEACHER) {
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setTeacherCode(generateTeacherCode());
            user.setTeacher(teacher);
        }
        if (userRequest.getRole() == CreateType.SALER) {
            saler = new Saler();
            saler.setUser(user);
            saler.setCode(generateSalerCode());
            user.setSale(saler);
        }

        userRepository.save(user);
        if (saler != null) {
            salerRepository.save(saler);
        }
//        String roleName1 = userRequest.getRole() == null ? null : userRequest.getRole().name();
        auditLogService.logCreate(roleName, user.getUsername(), username);
        if (userRequest.getRole() == CreateType.STUDENT) {
            adminStatsService.refreshMonthlyStats(user.getCreatedAt());
        }

        Long studentId = user.getStudent() != null ? user.getStudent().getUserId() : null;
        log.info("Created student id={}", studentId);
        return studentId;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public String uploadAvatar(MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long userId = jwt.getClaim("userId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        CloudinaryUploadResult result =
                cloudinaryService.uploadAvatar(avatar, userId.toString());

        UserProfile userProfile = user.getProfile();
        userProfile.setAvatarUrl(result.getUrl());
        userProfile.setAvatarPublicId(result.getPublicId());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        userProfileRepository.save(userProfile);
        return "Upload avatar successful!";
    }

    private String generateStudentCode() {
        String prefix = "STU";
        String maxCode = studentRepository.findMaxStudentCodeByPrefix(prefix);
        return nextSequentialCode(prefix, maxCode, 6);
    }

    private String generateTeacherCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String prefix = "TCH" + datePart;
        String maxCode = teacherRepository.findMaxTeacherCodeByPrefix(prefix);
        return nextSequentialCode(prefix, maxCode, 3);
    }

    private String generateSalerCode() {
        String prefix = "SAL";
        String maxCode = salerRepository.findMaxSalerCodeByPrefix(prefix);
        return nextSequentialCode(prefix, maxCode, 6);
    }

    private String nextSequentialCode(String prefix, String maxCode, int width) {
        int next = 1;
        if (maxCode != null && maxCode.startsWith(prefix)) {
            String numericPart = maxCode.substring(prefix.length());
            try {
                next = Integer.parseInt(numericPart) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        return prefix + String.format("%0" + width + "d", next);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public String deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userRepository.delete(user);
        return "Delete successful!";
    }

    private String resolveUserCodeByRole(UserProfile profile, CreateType role) {
        if (profile == null || profile.getUser() == null) {
            return null;
        }
        return switch (role) {
            case STUDENT -> {
                yield profile.getUser().getStudent() == null
                        ? null
                        : profile.getUser().getStudent().getStudentCode();
            }
            case TEACHER -> {
                yield profile.getUser().getTeacher() == null
                        ? null
                        : profile.getUser().getTeacher().getTeacherCode();
            }
            case SALER -> {
                yield profile.getUser().getSale() == null
                        ? null
                        : profile.getUser().getSale().getCode();
            }
            case ADMIN -> null;
        };
    }

    private String resolveUserCode(User user) {
        if (user == null) {
            return null;
        }
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        if (roles.contains(CreateType.STUDENT.name())) {
            return user.getStudent() == null ? null : user.getStudent().getStudentCode();
        }
        if (roles.contains(CreateType.TEACHER.name())) {
            return user.getTeacher() == null ? null : user.getTeacher().getTeacherCode();
        }
        if (roles.contains(CreateType.SALER.name())) {
            return user.getSale() == null ? null : user.getSale().getCode();
        }
        return user.getId() == null ? null : user.getId().toString();
    }
}
