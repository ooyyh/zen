package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.MessageTemplateCreateRequest;
import com.hbnu.zen.dto.MessageTemplateUpdateRequest;
import com.hbnu.zen.mybatis.entity.MessageTemplate;
import com.hbnu.zen.service.MessageTemplateService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/message-templates")
public class AdminMessageTemplateController {
    private final MessageTemplateService templateService;

    public AdminMessageTemplateController(MessageTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public ApiResponse<List<MessageTemplate>> list() {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(templateService.list());
    }

    @PostMapping
    public ApiResponse<MessageTemplate> create(@Validated @RequestBody MessageTemplateCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        MessageTemplate template = new MessageTemplate();
        template.setTemplateCode(request.getTemplateCode());
        template.setTitle(request.getTitle());
        template.setContent(request.getContent());
        return ApiResponse.success(templateService.create(template));
    }

    @PutMapping("/{id}")
    public ApiResponse<MessageTemplate> update(@PathVariable Long id,
                                               @Validated @RequestBody MessageTemplateUpdateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        MessageTemplate template = new MessageTemplate();
        template.setTitle(request.getTitle());
        template.setContent(request.getContent());
        return ApiResponse.success(templateService.update(id, template));
    }
}
