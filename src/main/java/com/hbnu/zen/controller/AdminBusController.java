package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.BusBookingView;
import com.hbnu.zen.dto.BusRouteCreateRequest;
import com.hbnu.zen.dto.BusTripCreateRequest;
import com.hbnu.zen.mybatis.entity.BusRoute;
import com.hbnu.zen.mybatis.entity.BusTrip;
import com.hbnu.zen.service.BusBookingService;
import com.hbnu.zen.service.BusRouteService;
import com.hbnu.zen.service.BusTripService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bus")
public class AdminBusController {
    private final BusRouteService busRouteService;
    private final BusTripService busTripService;
    private final BusBookingService bookingService;

    public AdminBusController(BusRouteService busRouteService, BusTripService busTripService, BusBookingService bookingService) {
        this.busRouteService = busRouteService;
        this.busTripService = busTripService;
        this.bookingService = bookingService;
    }

    @GetMapping("/routes")
    public ApiResponse<List<BusRoute>> routes() {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(busRouteService.list());
    }

    @PostMapping("/routes")
    public ApiResponse<BusRoute> createRoute(@Validated @RequestBody BusRouteCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        BusRoute route = new BusRoute();
        route.setName(request.getName());
        route.setOrigin(request.getOrigin());
        route.setDestination(request.getDestination());
        route.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        return ApiResponse.success(busRouteService.create(route));
    }

    @PutMapping("/routes/{id}")
    public ApiResponse<BusRoute> updateRoute(@PathVariable Long id,
                                             @Validated @RequestBody BusRouteCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        BusRoute route = new BusRoute();
        route.setId(id);
        route.setName(request.getName());
        route.setOrigin(request.getOrigin());
        route.setDestination(request.getDestination());
        route.setStatus(request.getStatus());
        return ApiResponse.success(busRouteService.update(route));
    }

    @GetMapping("/trips")
    public ApiResponse<List<BusTrip>> trips(@RequestParam(required = false) String status) {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(busTripService.list(status));
    }

    @PostMapping("/trips")
    public ApiResponse<BusTrip> createTrip(@Validated @RequestBody BusTripCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(busTripService.create(request));
    }

    @PutMapping("/trips/{id}")
    public ApiResponse<BusTrip> updateTrip(@PathVariable Long id,
                                           @Validated @RequestBody BusTripCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(busTripService.update(id, request));
    }

    @GetMapping("/bookings")
    public ApiResponse<List<BusBookingView>> bookings(@RequestParam(required = false) String status) {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(bookingService.listAll(status));
    }
}
