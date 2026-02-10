package com.example.demo.controller;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.k1.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserService userService;

//    @GetMapping("/debug")
//    public Map<String, Object> debug1(Authentication authentication) {
//        Jwt jwt = (Jwt) authentication.getPrincipal();
//
//        return Map.of(
//                "subject", jwt.getSubject(),
//                "authorities", authentication.getAuthorities(),
//                "claims", jwt.getClaims(),
//                "Authorities", authentication.getAuthorities()
//        );
//    }

    @PostMapping("/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<String> addUser(@RequestBody UserRequest userRequest){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long id = jwt.getClaim("userId");
        userRequest.setCreatedBy(id);
        return ApiResponse.<String>builder()
                .result(userService.addUser(userRequest))
                .build();
    }
}
