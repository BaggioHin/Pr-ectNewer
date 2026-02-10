package com.example.demo.service.impl;

import com.example.demo.dto.request.SubjectRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SubjectResponse;
import com.example.demo.entity.courseAndAcademic.Subject;
import com.example.demo.entity.courseAndAcademic.Course;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.service.k1.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExamRepository examRepository;

    @Override
    public SubjectResponse getSubjectByid(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(subject);
    }

    @Override
    public List<SubjectResponse> getSubjectByName(String name) {
        List<Subject> subjects = subjectRepository.findByNameContainingIgnoreCase(name);
        if (subjects.isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        return subjects.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<SubjectResponse> getListSubject(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Subject> pageResult = subjectRepository.findAll(pageable);
        List<SubjectResponse> data = pageResult.getContent()
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
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponse editSubject(Long id,SubjectRequest subjectRequest) {
        if (subjectRequest == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        if (subjectRequest.getCode() != null) {
            subject.setCode(subjectRequest.getCode());
        }
        if (subjectRequest.getName() != null) {
            subject.setName(subjectRequest.getName());
        }
        if (subjectRequest.getDescription() != null) {
            subject.setDescription(subjectRequest.getDescription());
        }
        if (subjectRequest.getCourseId() != null) {
            Course course = courseRepository.findById(subjectRequest.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
            subject.setCourse(course);
        }

        Subject saved = subjectRepository.save(subject);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponse addSubject(SubjectRequest subjectRequest) {
        Course course = courseRepository.findById(subjectRequest.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        Subject subject = new Subject();
        subject.setCode(subjectRequest.getCode());
        subject.setName(subjectRequest.getName());
        subject.setDescription(subjectRequest.getDescription());
        subject.setCourse(course);

        Subject saved = subjectRepository.save(subject);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (examRepository.existsBySubjectId(id)) {
            throw new AppException(ErrorCode.SUBJECT_IN_USE);
        }
        subjectRepository.delete(subject);
        return "Delete successful!";
    }

    private SubjectResponse toResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .code(subject.getCode())
                .name(subject.getName())
                .description(subject.getDescription())
                .courseId(subject.getCourse() != null ? subject.getCourse().getId() : null)
                .build();
    }
}
