package com.example.demo.service.k1;

import com.example.demo.dto.response.EnrollmentReponse;
import com.example.demo.dto.response.PageResponse;


public interface EnrollmentService {
    EnrollmentReponse getEnrollmentById(Long id);
    PageResponse<EnrollmentReponse> getListEnrollment(int page,int size);

    EnrollmentReponse addEnrollment(Long courseClassId,Long studentId);
    EnrollmentReponse changeStatusEnrollment(Long id, String status);

    EnrollmentReponse createEnrollmentForPayment(Long courseClassId, Long studentId, Long salerUserId);
}
