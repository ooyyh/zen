package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.dto.DashboardSummary;
import com.hbnu.zen.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ApiResponse<DashboardSummary> summary() {
        return ApiResponse.success(dashboardService.getSummary(AuthUtil.getUserId()));
    }
}
