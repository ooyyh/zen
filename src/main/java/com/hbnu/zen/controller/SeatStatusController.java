package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.service.SeatReservationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seat-status")
public class SeatStatusController {
    private final SeatReservationService seatReservationService;

    public SeatStatusController(SeatReservationService seatReservationService) {
        this.seatReservationService = seatReservationService;
    }

    @PostMapping("/pending/{seatId}")
    public ApiResponse<Void> markPending(@PathVariable Long seatId) {
        seatReservationService.markSeatPending(seatId, AuthUtil.getUserId());
        return ApiResponse.success(null);
    }

    @PostMapping("/release/{seatId}")
    public ApiResponse<Void> release(@PathVariable Long seatId) {
        seatReservationService.releaseSeatPending(seatId, AuthUtil.getUserId());
        return ApiResponse.success(null);
    }

    @GetMapping("/pending/{studyRoomId}")
    public ApiResponse<java.util.Map<Long, java.util.Map<String, Object>>> getPendingSeats(@PathVariable Long studyRoomId) {
        return ApiResponse.success(seatReservationService.getAllPendingSeats(studyRoomId));
    }
}
