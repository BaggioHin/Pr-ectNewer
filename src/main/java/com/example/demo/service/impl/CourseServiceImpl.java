package com.example.demo.service.impl;

import com.example.demo.dto.request.CourseRequest;
import com.example.demo.dto.response.CourseResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.courseAndAcademic.Course;
import com.example.demo.entity.courseAndAcademic.Subject;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.service.k1.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseClassRepository courseClassRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(course);
    }

    @Override
    public CourseResponse getCourseByName(String name) {
        Course course = courseRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toResponse(course);
    }

    @Override
    public PageResponse<CourseResponse> getListCourse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Course> pageResult = courseRepository.findAll(pageable);
        List<CourseResponse> data = pageResult.getContent()
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
    public CourseResponse editCourse(Long id, CourseRequest courseRequest) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        applyRequest(courseRequest, course);
        Course saved = courseRepository.save(course);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CourseResponse addCourse(CourseRequest courseRequest) {
        Course course = new Course();
        applyRequest(courseRequest, course);
        Course saved = courseRepository.save(course);
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        courseRepository.delete(course);
        return "Delete successful!";
    }





























    @Override
    public String addCourseClass(Long courseId, Long classId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        CourseClass courseClass = courseClassRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));

        courseClass.setCourse(course);
        courseClassRepository.save(courseClass);
        return "Add course class successful!";
    }

    @Override
    public String deleteCourseClass(Long courseId, Long classId) {
        CourseClass courseClass = courseClassRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));
        if (courseClass.getCourse() != null
                && courseClass.getCourse().getId() != null
                && courseClass.getCourse().getId().equals(courseId)) {
            courseClass.setCourse(null);
            courseClassRepository.save(courseClass);
        }
        return "Delete course class successful!";
    }

    @Override
    public String addSubject(Long courseId, Long subjectId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        subject.setCourse(course);
        subjectRepository.save(subject);
        return "Add subject successful!";
    }

    @Override
    public String deleteSubject(Long courseId, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        if (subject.getCourse() != null
                && subject.getCourse().getId() != null
                && subject.getCourse().getId().equals(courseId)) {
            subject.setCourse(null);
            subjectRepository.save(subject);
        }
        return "Delete subject successful!";
    }

    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .description(course.getDescription())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .statusCourse(course.getStatusCourse())
                .credit(course.getCredit())
                .build();
    }

    private void applyRequest(CourseRequest request, Course course) {
        if (request == null) {
            return;
        }
        if (request.getCode() != null) {
            course.setCode(request.getCode());
        }
        if (request.getName() != null) {
            course.setName(request.getName());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            course.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            course.setEndDate(request.getEndDate());
        }
        if (request.getStatusCourse() != null) {
            course.setStatusCourse(request.getStatusCourse());
        }
        if (request.getCredit() != null) {
            course.setCredit(request.getCredit());
        }
    }
}
