package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.dto.SeatReservationRequest;
import com.hbnu.zen.dto.SeatReservationView;
import com.hbnu.zen.dto.SeatView;
import com.hbnu.zen.dto.StudyRoomView;
import com.hbnu.zen.mybatis.entity.SeatReservation;
import com.hbnu.zen.mybatis.entity.StudyRoom;
import com.hbnu.zen.service.SeatReservationService;
import com.hbnu.zen.service.StudyRoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/study-rooms")
public class StudyRoomController {
    private final StudyRoomService studyRoomService;
    private final SeatReservationService reservationService;

    public StudyRoomController(StudyRoomService studyRoomService, SeatReservationService reservationService) {
        this.studyRoomService = studyRoomService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public ApiResponse<List<StudyRoom>> list() {
        return ApiResponse.success(studyRoomService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<StudyRoom> detail(@PathVariable Long id) {
        return ApiResponse.success(studyRoomService.getById(id));
    }

    @GetMapping("/available")
    public ApiResponse<List<StudyRoomView>> available(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ApiResponse.success(studyRoomService.listAvailable(startTime, endTime));
    }

    @GetMapping("/{roomId}/seats")
    public ApiResponse<List<SeatView>> seats(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ApiResponse.success(reservationService.listAvailableSeats(roomId, startTime, endTime));
    }

    @PostMapping("/seats/{seatId}/reserve")
    public ApiResponse<SeatReservation> reserve(@PathVariable Long seatId,
                                                @Validated @RequestBody SeatReservationRequest request) {
        return ApiResponse.success(reservationService.reserve(seatId, AuthUtil.getUserId(), request));
    }

    @PostMapping("/reservations/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        reservationService.cancel(id, AuthUtil.getUserId());
        return ApiResponse.success();
    }

    @PostMapping("/reservations/{id}/check-in")
    public ApiResponse<Void> checkIn(@PathVariable Long id) {
        reservationService.checkIn(id, AuthUtil.getUserId());
        return ApiResponse.success();
    }

    @GetMapping("/reservations/my")
    public ApiResponse<List<SeatReservationView>> myReservations() {
        return ApiResponse.success(reservationService.listMy(AuthUtil.getUserId()));
    }
}
