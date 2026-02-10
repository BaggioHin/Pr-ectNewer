package com.example.demo.service.impl;

import com.example.demo.dto.request.CourseClassRequest;
import com.example.demo.dto.response.CourseClassResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.classAndLearn.ClassSchedule;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.CourseClassMapper;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.ClassScheduleRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.k1.CourseClassService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseClassServiceImpl implements CourseClassService {

    @Autowired
    CourseClassRepository courseClassRepository;
    @Autowired
    CourseClassMapper courseClassMapper;
    @Autowired
    CourseRepository courseRepository;


    @Override
    public CourseClassResponse getCourseClassById(Long id) {
        CourseClass courseClass = courseClassRepository.findById(id).get();
        return courseClassMapper.entityToResponse(courseClass);
    }

    @Override
    public List<CourseClassResponse> getCourseClassByName(String name) {
        List<CourseClass> courseClasses = courseClassRepository.findByName(name);
        return courseClassMapper.entityToListResponse(courseClasses);
    }

    @Override
    @Transactional
    public PageResponse<CourseClassResponse> getListCourseClass(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CourseClass> pageResult = courseClassRepository.findAll(pageable);
        List<CourseClassResponse> data = courseClassMapper.entityToListResponse(pageResult.getContent());

        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    public CourseClassResponse addCourseClass(CourseClassRequest request) {
        if (request == null || request.getCourseId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        CourseClass courseClass = new CourseClass();
        courseClassMapper.requestToEntity(request, courseClass);
        var course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        courseClass.setCourse(course);
        courseClass.setStatusCourse(request.getStatusCourse());
        courseClass.setDescription(request.getDescription());


        CourseClass saved = courseClassRepository.save(courseClass);
        return courseClassMapper.entityToResponse(saved);
    }

    @Override
    public CourseClassResponse editCourseClass(Long id, CourseClassRequest request) {

        CourseClass courseClass = courseClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));

        courseClassMapper.requestToEntity(request, courseClass);
        if (request != null && request.getCourseId() != null) {
            var course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
            courseClass.setCourse(course);
        }

        CourseClass saved = courseClassRepository.save(courseClass);
        return courseClassMapper.entityToResponse(saved);
    }


    @Override
    public String deleteCourseClass(Long id) {
        courseClassRepository.deleteById(id);
        return "Delete successful!";
    }

}
