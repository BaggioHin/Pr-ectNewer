package com.example.demo.service.impl;

import com.example.demo.constant.CreateType;
import com.example.demo.constant.StatusUser;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.PageResponse;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
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

    @Override
    public UserResponse getUserByid(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UserProfile profile = user.getProfile();
        if (profile == null) {
            throw new AppException(ErrorCode.USERPROFILE_NOT_EXISTED);
        }
        return userMapper.userToUserResponse(profile);
    }

    @Override
    public List<UserResponse> getUserByName(String name) {
        List<UserProfile> userProfile = userProfileRepository.findByName(name);
        return userMapper.userToUserResponses(userProfile);
    }

    @Override
    public PageResponse<UserResponse> getListUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<UserProfile> pageResult = userProfileRepository.findAll(pageable);

        List<UserResponse> data =
                userMapper.userToUserResponses(pageResult.getContent());

        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
//    @PreAuthorize("hasAuthority('SCOPE_STUDENT')")
    @PreAuthorize("isAuthenticated()")
    public String editUser(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        if (userRequest.getEmail() != null && userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userRequest.getPhone() != null && userRepository.existsByPhone(userRequest.getPhone())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long userId = jwt.getClaim("userId");

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
    @PreAuthorize("hasRole('ADMIN')")
    public String addUser(UserRequest userRequest) {
        if (userRequest.getUsername() == null || userRequest.getUsername().isBlank()) {
            throw new AppException(ErrorCode.USERNAME_INVALID);
        }

        if (userRequest.getPassword() == null || userRequest.getPassword().isBlank()) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

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
        user.setCreatedBy(userRequest.getCreatedBy());

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
        if (userRequest.getRole() == CreateType.SALE) {
            saler = new Saler();
            saler.setUser(user);
            saler.setCode(generateSalerCode());
        }
        userRepository.save(user);
        if (saler != null) {
            salerRepository.save(saler);
        }

        return "Add successful!";
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
}
