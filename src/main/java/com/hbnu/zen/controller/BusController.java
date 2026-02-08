package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.dto.BusBookingView;
import com.hbnu.zen.dto.BusTripView;
import com.hbnu.zen.mybatis.entity.BusBooking;
import com.hbnu.zen.service.BusBookingService;
import com.hbnu.zen.service.BusTripService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bus")
public class BusController {
    private final BusTripService busTripService;
    private final BusBookingService bookingService;

    public BusController(BusTripService busTripService, BusBookingService bookingService) {
        this.busTripService = busTripService;
        this.bookingService = bookingService;
    }

    @GetMapping("/trips")
    public ApiResponse<List<BusTripView>> listTrips(@RequestParam(required = false) String status) {
        return ApiResponse.success(busTripService.listViews(status));
    }

    @PostMapping("/trips/{id}/book")
    public ApiResponse<BusBooking> book(@PathVariable Long id) {
        return ApiResponse.success(bookingService.book(id, AuthUtil.getUserId()));
    }

    @PostMapping("/bookings/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        bookingService.cancel(id, AuthUtil.getUserId());
        return ApiResponse.success();
    }

    @GetMapping("/bookings/my")
    public ApiResponse<List<BusBookingView>> my() {
        return ApiResponse.success(bookingService.listMy(AuthUtil.getUserId()));
    }
}
