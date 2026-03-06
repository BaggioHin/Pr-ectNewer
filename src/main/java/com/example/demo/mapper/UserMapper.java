package com.example.demo.mapper;

import com.example.demo.dto.request.StudentRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.StudentResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.authAndUser.UserProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @IterableMapping(qualifiedByName = "userToUserResponse")
    List<UserResponse> userToUserResponses(List<UserProfile> userProfiles);

    @Named("userToUserResponse")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "avatarUrl", target = "avatar")
    UserResponse userToUserResponse(UserProfile userProfiles);

    default String map(Long value) {
        return value == null ? null : value.toString();
    }

//    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void requestToUser(
            UserRequest request,
            @MappingTarget User user
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void requestToUser(
            UserUpdateRequest request,
            @MappingTarget User user
    );
}
