package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.ReservationRules;
import com.hbnu.zen.service.ConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/config")
public class ConfigController {
    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/reservation-rules")
    public ApiResponse<ReservationRules> rules() {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(configService.getReservationRules());
    }

    @PutMapping("/reservation-rules")
    public ApiResponse<Void> update(@Validated @RequestBody ReservationRules rules) {
        AuthUtil.requireRole(Role.ADMIN);
        configService.updateReservationRules(rules);
        return ApiResponse.success();
    }
}
