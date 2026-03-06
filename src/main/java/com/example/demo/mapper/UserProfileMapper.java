package com.example.demo.mapper;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.entity.authAndUser.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    @Mapping(target = "theme", source = "theme")
    @Mapping(target = "language", source = "language")
    @Mapping(target = "user", ignore = true)
    void requestToUserProfile(
            UserRequest request,
            @MappingTarget UserProfile userProfile
    );

    @Mapping(target = "theme", source = "theme")
    @Mapping(target = "language", source = "language")
    @Mapping(target = "user", ignore = true)
    void requestToUserProfile(
            UserUpdateRequest request,
            @MappingTarget UserProfile userProfile
    );
}
