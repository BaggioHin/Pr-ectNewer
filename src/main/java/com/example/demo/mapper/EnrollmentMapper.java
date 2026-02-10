package com.example.demo.mapper;

//import com.example.demo.dto.response.EnrollmentReponse;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.EnrollmentReponse;
import com.example.demo.entity.authAndUser.UserProfile;
import com.example.demo.entity.loginAndProcess.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    EnrollmentReponse entityToResponse(Enrollment enrollment);

    List<EnrollmentReponse> entityToListResponse(List<Enrollment> enrollment);

    void requestToUserProfile(
            UserRequest request,
            @MappingTarget UserProfile userProfile
    );

}
