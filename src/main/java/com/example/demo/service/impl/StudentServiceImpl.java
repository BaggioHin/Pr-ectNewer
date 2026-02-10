package com.example.demo.service.impl;

//package com.example.demo.service.impl;

import com.example.demo.dto.response.StudentResponse;
import com.example.demo.entity.authAndUser.UserProfile;
import com.example.demo.entity.people.Student;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.service.k1.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Override
    public List<StudentResponse> getStudentByCourseClass(Long courseClassId) {
        var enrollments = enrollmentRepository.findByCourseClass_Id(courseClassId);
        if (enrollments.isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }

        Map<Long, StudentResponse> unique = new LinkedHashMap<>();
        for (var enrollment : enrollments) {
            Student student = enrollment.getStudent();
            if (student == null || student.getUser() == null) {
                continue;
            }
            var user = student.getUser();
            UserProfile profile = user.getProfile();
            StudentResponse response = StudentResponse.builder()
                    .userId(user.getId())
                    .fullname(profile != null ? profile.getName() : null)
                    .dob(profile != null ? profile.getDob() : null)
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .thumbnailUrl(profile != null ? profile.getAvatar() : null)
                    .build();
            unique.putIfAbsent(user.getId(), response);
        }
        return List.copyOf(unique.values());
    }
}
