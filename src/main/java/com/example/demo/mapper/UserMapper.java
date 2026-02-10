package com.example.demo.mapper;

import com.example.demo.dto.request.StudentRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.StudentResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.authAndUser.UserProfile;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.id", target = "id")
    List<UserResponse> userToUserResponses(List<UserProfile> userProfiles);


    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.email", target = "email")
    UserResponse userToUserResponse(UserProfile userProfiles);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void requestToUser(
            UserRequest request,
            @MappingTarget User user
    );
}
