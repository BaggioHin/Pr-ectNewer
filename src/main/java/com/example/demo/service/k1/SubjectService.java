package com.example.demo.service.k1;

import com.example.demo.dto.request.SubjectRequest;
import com.example.demo.dto.response.SubjectResponse;

import com.example.demo.dto.response.PageResponse;

import java.util.List;

public interface SubjectService {
     SubjectResponse getSubjectByid(Long id);

     List<SubjectResponse> getSubjectByName(String name);

     PageResponse<SubjectResponse> getListSubject(int page, int size);

     SubjectResponse editSubject(Long id,SubjectRequest subjectRequest);

     SubjectResponse addSubject(SubjectRequest subjectRequest);

     String deleteSubject(Long id) ;
}
