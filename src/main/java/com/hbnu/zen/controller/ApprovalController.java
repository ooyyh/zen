package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.ApprovalDecisionRequest;
import com.hbnu.zen.dto.ApprovalView;
import com.hbnu.zen.service.ApprovalService;
import com.hbnu.zen.service.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/approvals")
public class ApprovalController {
    private final ApprovalService approvalService;
    private final ReservationService reservationService;

    public ApprovalController(ApprovalService approvalService, ReservationService reservationService) {
        this.approvalService = approvalService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public ApiResponse<List<ApprovalView>> listPending() {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(approvalService.listPendingByRole(Role.ADMIN));
    }

    @PostMapping("/{reservationId}/approve")
    public ApiResponse<Void> approve(@PathVariable Long reservationId,
                                     @RequestBody(required = false) ApprovalDecisionRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        String remark = request == null ? null : request.getRemark();
        reservationService.approveReservation(reservationId, AuthUtil.getUserId(), remark);
        return ApiResponse.success();
    }

    @PostMapping("/{reservationId}/reject")
    public ApiResponse<Void> reject(@PathVariable Long reservationId,
                                    @RequestBody(required = false) ApprovalDecisionRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        String remark = request == null ? null : request.getRemark();
        reservationService.rejectReservation(reservationId, AuthUtil.getUserId(), remark);
        return ApiResponse.success();
    }
}
