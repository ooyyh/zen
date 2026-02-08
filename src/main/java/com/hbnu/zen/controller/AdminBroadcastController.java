package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.BroadcastRequest;
import com.hbnu.zen.service.MessageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/messages")
public class AdminBroadcastController {
    private final MessageService messageService;

    public AdminBroadcastController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/broadcast")
    public ApiResponse<Integer> broadcast(@Validated @RequestBody BroadcastRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        int sent = messageService.broadcast(request.getTitle(), request.getContent(), request.getTargetRole());
        return ApiResponse.success(sent);
    }
}
