package com.example.demo.service.impl;

import com.example.demo.dto.request.StudentProgressRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.StudentProgressResponse;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.loginAndProcess.StudentProgress;
import com.example.demo.entity.loginAndProcess.StudentProgressId;
import com.example.demo.entity.people.Student;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.StudentProgressRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.k1.StudentProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentProgressServiceImpl implements StudentProgressService {
    @Autowired
    StudentProgressRepository studentProgressRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    CourseClassRepository courseClassRepository;

    @Override
    public StudentProgressResponse getStudentProgress(Long studentId, Long courseClassId) {
        StudentProgress progress = studentProgressRepository.findById(
                        new StudentProgressId(studentId, courseClassId))
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(progress);
    }

    @Override
    public PageResponse<StudentProgressResponse> getListStudentProgress(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("courseClass.id").descending());
        Page<StudentProgress> pageResult = studentProgressRepository.findAll(pageable);
        List<StudentProgressResponse> data = pageResult.getContent()
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
    public StudentProgressResponse addStudentProgress(StudentProgressRequest request) {
        if (request == null || request.getStudentId() == null || request.getCourseClassId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        StudentProgressId id = new StudentProgressId(request.getStudentId(), request.getCourseClassId());
        if (studentProgressRepository.existsById(id)) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        CourseClass courseClass = courseClassRepository.findById(request.getCourseClassId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));

        StudentProgress progress = new StudentProgress();
        progress.setId(id);
        progress.setStudent(student);
        progress.setCourseClass(courseClass);
        progress.setCompletionPercent(request.getCompletionPercent() != null ? request.getCompletionPercent() : 0.0);
        progress.setUpdatedAt(LocalDateTime.now());

        StudentProgress saved = studentProgressRepository.save(progress);
        return toResponse(saved);
    }

    @Override
    public String deleteStudentProgress(Long studentId, Long courseClassId) {
        StudentProgress progress = studentProgressRepository.findById(
                        new StudentProgressId(studentId, courseClassId))
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        studentProgressRepository.delete(progress);
        return "Delete successful!";
    }

    private StudentProgressResponse toResponse(StudentProgress progress) {
        return StudentProgressResponse.builder()
                .studentId(progress.getId() != null ? progress.getId().getStudentId() : null)
                .courseClassId(progress.getId() != null ? progress.getId().getCourseClassId() : null)
                .completionPercent(progress.getCompletionPercent())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
}
