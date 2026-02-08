package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.dto.LoginRequest;
import com.hbnu.zen.dto.LoginResponse;
import com.hbnu.zen.dto.UserView;
import com.hbnu.zen.mybatis.entity.User;
import com.hbnu.zen.security.JwtUtil;
import com.hbnu.zen.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        String token = jwtUtil.generateToken(claims);
        LoginResponse response = new LoginResponse(token, toView(user));
        return ApiResponse.success(response);
    }

    @PostMapping("/me")
    public ApiResponse<UserView> me() {
        User user = userService.getById(AuthUtil.getUserId());
        return ApiResponse.success(toView(user));
    }

    private UserView toView(User user) {
        UserView view = new UserView();
        view.setId(user.getId());
        view.setUsername(user.getUsername());
        view.setRole(user.getRole());
        view.setStatus(user.getStatus());
        return view;
    }
}
