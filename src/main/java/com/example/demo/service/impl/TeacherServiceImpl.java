package com.example.demo.service.impl;

import com.example.demo.dto.response.TeacherResponse;
import com.example.demo.entity.classAndLearn.TeachingAssignment;
import com.example.demo.entity.people.Teacher;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.TeachingAssignmentRepository;
import com.example.demo.service.k1.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    TeachingAssignmentRepository teachingAssignmentRepository;

    @Override
    public List<TeacherResponse> getTeacherByCourseClass(Long courseClassId) {
        List<TeachingAssignment> assignments =
                teachingAssignmentRepository.findByCourseClass_Id(courseClassId);
        if (assignments.isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }

        Map<Long, TeacherResponse> unique = new LinkedHashMap<>();
        for (TeachingAssignment assignment : assignments) {
            Teacher teacher = assignment.getTeacher();
            if (teacher == null) {
                continue;
            }
            TeacherResponse response = TeacherResponse.builder()
                    .userId(teacher.getUserId())
                    .teacherCode(teacher.getTeacherCode())
                    .build();
            unique.putIfAbsent(teacher.getUserId(), response);
        }

        return List.copyOf(unique.values());
    }
}
