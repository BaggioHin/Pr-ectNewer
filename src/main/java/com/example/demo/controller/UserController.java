package com.example.demo.controller;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.k1.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @Operation(summary = "Get user by Id")
    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable Long id){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByid(id))
                .build();
    }


    @Operation(summary = "Get user by name")
    @GetMapping("/search")
    ApiResponse<List<UserResponse>> getUserByName(@RequestParam String name){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUserByName(name))
                .build();
    }


    @Operation(summary = "Get List User")
    @GetMapping
    ApiResponse<PageResponse<UserResponse>> getUserByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(userService.getListUser(page,size))
                .build();
    }


    @Operation(summary = "Update user")
    @PutMapping
    ApiResponse<String> editUser(@RequestBody UserRequest UserRequest){
        return ApiResponse.<String>builder()
                .result(userService.editUser(UserRequest))
                .build();
    }


    @Operation(summary = "Add user")
    @PostMapping
    ApiResponse<String> addUser(@RequestBody UserRequest UserRequest){
        return ApiResponse.<String>builder()
                .result(userService.addUser(UserRequest))
                .build();
    }


    @Operation(summary = "Delete User")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    ApiResponse<String> deleteUser(@PathVariable Long id){
        return ApiResponse.<String>builder()
                .result(userService.deleteUser(id))
                .build();
    }
}
