package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.UserCreateRequest;
import com.hbnu.zen.dto.UserView;
import com.hbnu.zen.mybatis.entity.User;
import com.hbnu.zen.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserView>> list() {
        AuthUtil.requireRole(Role.ADMIN);
        List<User> users = userService.listAll();
        List<UserView> result = new ArrayList<>();
        for (User user : users) {
            result.add(toView(user));
        }
        return ApiResponse.success(result);
    }

    @PostMapping
    public ApiResponse<UserView> create(@Validated @RequestBody UserCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        User user = userService.createUser(request.getUsername(), request.getPassword(), request.getRole());
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
