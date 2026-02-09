package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.dto.UserView;
import com.hbnu.zen.mybatis.entity.User;
import com.hbnu.zen.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class CurrentUserController {
    private final UserService userService;

    public CurrentUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ApiResponse<UserView> profile() {
        Long userId = AuthUtil.getUserId();
        User user = userService.getById(userId);
        
        UserView view = new UserView();
        view.setId(user.getId());
        view.setUsername(user.getUsername());
        view.setRole(user.getRole());
        view.setStatus(user.getStatus());
        
        return ApiResponse.success(view);
    }
}
