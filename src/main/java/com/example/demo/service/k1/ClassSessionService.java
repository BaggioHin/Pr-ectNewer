package com.example.demo.service.k1;

import com.example.demo.dto.request.ClassSessionRequest;
import com.example.demo.dto.response.ClassSessionResponse;
import com.example.demo.dto.response.PageResponse;

public interface ClassSessionService {
    ClassSessionResponse getClassSessionById(Long id);

    PageResponse<ClassSessionResponse> getListClassSession(int page, int size);

    ClassSessionResponse addClassSession(ClassSessionRequest request);

    ClassSessionResponse editClassSession(Long id, ClassSessionRequest request);

    String deleteClassSession(Long id);

    ClassSessionResponse addMakeupSession(ClassSessionRequest request);

    ClassSessionResponse editMakeupSession(Long id, ClassSessionRequest request);

    ClassSessionResponse getMakeupSessionById(Long id);

    java.util.List<ClassSessionResponse> getMakeupSessionsByCourseClass(Long courseClassId);
}
