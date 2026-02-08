package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.CreateReservationRequest;
import com.hbnu.zen.mybatis.entity.Reservation;
import com.hbnu.zen.service.ReservationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ApiResponse<Reservation> create(@Validated @RequestBody CreateReservationRequest request) {
        Reservation reservation = reservationService.createReservation(AuthUtil.getUserId(), request);
        return ApiResponse.success(reservation);
    }

    @GetMapping("/my")
    public ApiResponse<List<Reservation>> my() {
        return ApiResponse.success(reservationService.listByUser(AuthUtil.getUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Reservation> detail(@PathVariable Long id) {
        Reservation reservation = reservationService.getById(id);
        if (!Role.ADMIN.equals(AuthUtil.getRole()) && !reservation.getUserId().equals(AuthUtil.getUserId())) {
            throw new com.hbnu.zen.common.BusinessException(403, "无权限查看该预约");
        }
        return ApiResponse.success(reservation);
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        boolean admin = Role.ADMIN.equals(AuthUtil.getRole());
        reservationService.cancelReservation(id, AuthUtil.getUserId(), admin);
        return ApiResponse.success();
    }
}
