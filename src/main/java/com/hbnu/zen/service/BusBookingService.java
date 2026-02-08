package com.hbnu.zen.service;

import com.hbnu.zen.common.BusBookingStatus;
import com.hbnu.zen.common.BusTripStatus;
import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.dto.BusBookingView;
import com.hbnu.zen.mapper.BusBookingMapper;
import com.hbnu.zen.mapper.BusRouteMapper;
import com.hbnu.zen.mapper.BusTripMapper;
import com.hbnu.zen.mybatis.entity.BusBooking;
import com.hbnu.zen.mybatis.entity.BusRoute;
import com.hbnu.zen.mybatis.entity.BusTrip;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class BusBookingService {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final BusBookingMapper bookingMapper;
    private final BusTripMapper busTripMapper;
    private final BusRouteMapper busRouteMapper;
    private final MessageService messageService;
    private final RedissonClient redissonClient;
    private final long lockWaitMs;
    private final long lockLeaseMs;

    public BusBookingService(BusBookingMapper bookingMapper,
                             BusTripMapper busTripMapper,
                             BusRouteMapper busRouteMapper,
                             MessageService messageService,
                             RedissonClient redissonClient,
                             @Value("${app.bus.lock-wait-ms}") long lockWaitMs,
                             @Value("${app.bus.lock-lease-ms}") long lockLeaseMs) {
        this.bookingMapper = bookingMapper;
        this.busTripMapper = busTripMapper;
        this.busRouteMapper = busRouteMapper;
        this.messageService = messageService;
        this.redissonClient = redissonClient;
        this.lockWaitMs = lockWaitMs;
        this.lockLeaseMs = lockLeaseMs;
    }

    @Transactional
    public BusBooking book(Long tripId, Long userId) {
        BusTrip trip = busTripMapper.selectById(tripId);
        if (trip == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班次不存在");
        }
        if (!BusTripStatus.OPEN.equals(trip.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "班次未开放");
        }
        if (trip.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "班次已发车，无法预约");
        }
        String lockKey = "lock:bus:trip:" + tripId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked;
        try {
            locked = lock.tryLock(lockWaitMs, lockLeaseMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SERVER_ERROR, "系统繁忙，请稍后再试");
        }
        if (!locked) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "预约过于频繁，请稍后重试");
        }
        try {
            BusBooking existing = bookingMapper.selectByTripAndUser(tripId, userId);
            if (existing != null && !BusBookingStatus.CANCELED.equals(existing.getStatus())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "已预约或候补，无需重复操作");
            }
            int booked = bookingMapper.countBooked(tripId, BusBookingStatus.BOOKED);
            String status = booked < trip.getCapacity() ? BusBookingStatus.BOOKED : BusBookingStatus.WAITLIST;
            if (existing != null) {
                bookingMapper.updateStatus(existing.getId(), status);
            } else {
                BusBooking booking = new BusBooking();
                booking.setTripId(tripId);
                booking.setUserId(userId);
                booking.setStatus(status);
                bookingMapper.insert(booking);
                existing = booking;
            }
            sendBookingMessage(userId, trip, status);
            return existing;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void cancel(Long bookingId, Long userId) {
        BusBooking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "预约不存在");
        }
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限取消");
        }
        BusTrip trip = busTripMapper.selectById(booking.getTripId());
        if (trip == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班次不存在");
        }
        if (trip.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "班次已发车，无法取消");
        }
        String lockKey = "lock:bus:trip:" + booking.getTripId();
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked;
        try {
            locked = lock.tryLock(lockWaitMs, lockLeaseMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SERVER_ERROR, "系统繁忙，请稍后再试");
        }
        if (!locked) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "操作过于频繁，请稍后重试");
        }
        try {
            boolean wasBooked = BusBookingStatus.BOOKED.equals(booking.getStatus());
            bookingMapper.updateStatus(bookingId, BusBookingStatus.CANCELED);
            sendCancelMessage(userId, trip);
            if (wasBooked) {
                BusBooking waitlist = bookingMapper.selectEarliestWaitlist(trip.getId(), BusBookingStatus.WAITLIST);
                if (waitlist != null) {
                    bookingMapper.updateStatus(waitlist.getId(), BusBookingStatus.BOOKED);
                    sendPromoteMessage(waitlist.getUserId(), trip);
                }
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public List<BusBookingView> listMy(Long userId) {
        return bookingMapper.selectMyViews(userId);
    }

    public List<BusBookingView> listAll(String status) {
        return bookingMapper.selectAllViews(status);
    }

    private void sendBookingMessage(Long userId, BusTrip trip, String status) {
        Map<String, String> params = new HashMap<>();
        params.put("route", buildRouteName(trip));
        params.put("departureTime", trip.getDepartureTime().format(TIME_FORMATTER));
        if (BusBookingStatus.BOOKED.equals(status)) {
            messageService.sendTemplate(userId, "BUS_BOOKED", params);
        } else {
            messageService.sendTemplate(userId, "BUS_WAITLIST", params);
        }
    }

    private void sendCancelMessage(Long userId, BusTrip trip) {
        Map<String, String> params = new HashMap<>();
        params.put("route", buildRouteName(trip));
        params.put("departureTime", trip.getDepartureTime().format(TIME_FORMATTER));
        messageService.sendTemplate(userId, "BUS_CANCELED", params);
    }

    private void sendPromoteMessage(Long userId, BusTrip trip) {
        Map<String, String> params = new HashMap<>();
        params.put("route", buildRouteName(trip));
        params.put("departureTime", trip.getDepartureTime().format(TIME_FORMATTER));
        messageService.sendTemplate(userId, "BUS_PROMOTED", params);
    }

    private String buildRouteName(BusTrip trip) {
        if (trip == null) {
            return "未知线路";
        }
        BusRoute route = busRouteMapper.selectById(trip.getRouteId());
        if (route == null) {
            return "线路" + trip.getRouteId();
        }
        return route.getOrigin() + " → " + route.getDestination();
    }
}
