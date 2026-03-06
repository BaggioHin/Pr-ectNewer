package com.example.demo.service.k1;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.UserMeResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.constant.CreateType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserResponse getUserByid(Long id);

    List<UserResponse> getUserByName(String name);

    List<UserResponse> getUserByRole(CreateType role);

    PageResponse<UserResponse> getListUser(int page, int size);

    UserMeResponse getMyInfo();

    String editUser(UserUpdateRequest userRequest);

    String changePassword(ChangePasswordRequest request);

    String requestPasswordResetForCurrentUser();

    Long addUser(UserRequest userRequest);

    String uploadAvatar(MultipartFile avatar);

    String deleteUser(Long id);
}
