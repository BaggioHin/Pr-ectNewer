package com.example.demo.service.k1;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getUserByid(Long id);

    List<UserResponse> getUserByName(String name);

    PageResponse<UserResponse> getListUser(int page, int size);

    String editUser(UserRequest userRequest);

    String addUser(UserRequest userRequest);

    String deleteUser(Long id);
}
