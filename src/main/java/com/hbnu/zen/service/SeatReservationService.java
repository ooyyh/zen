package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.common.SeatReservationStatus;
import com.hbnu.zen.dto.SeatReservationRequest;
import com.hbnu.zen.dto.SeatReservationView;
import com.hbnu.zen.dto.SeatView;
import com.hbnu.zen.mapper.SeatMapper;
import com.hbnu.zen.mapper.SeatReservationMapper;
import com.hbnu.zen.mapper.StudyRoomMapper;
import com.hbnu.zen.mybatis.entity.Seat;
import com.hbnu.zen.mybatis.entity.SeatReservation;
import com.hbnu.zen.mybatis.entity.StudyRoom;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SeatReservationService {
    private final SeatMapper seatMapper;
    private final SeatReservationMapper reservationMapper;
    private final StudyRoomMapper studyRoomMapper;
    private final MessageService messageService;
    private final RedissonClient redissonClient;
    private final long lockWaitMs;
    private final long lockLeaseMs;

    public SeatReservationService(SeatMapper seatMapper,
                                  SeatReservationMapper reservationMapper,
                                  StudyRoomMapper studyRoomMapper,
                                  MessageService messageService,
                                  RedissonClient redissonClient,
                                  @Value("${app.seat.lock-wait-ms:800}") long lockWaitMs,
                                  @Value("${app.seat.lock-lease-ms:5000}") long lockLeaseMs) {
        this.seatMapper = seatMapper;
        this.reservationMapper = reservationMapper;
        this.studyRoomMapper = studyRoomMapper;
        this.messageService = messageService;
        this.redissonClient = redissonClient;
        this.lockWaitMs = lockWaitMs;
        this.lockLeaseMs = lockLeaseMs;
    }

    public List<SeatView> listAvailableSeats(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<String> blockingStatuses = Arrays.asList(SeatReservationStatus.RESERVED, SeatReservationStatus.CHECKED_IN);
        return seatMapper.selectAvailableSeats(roomId, startTime, endTime, blockingStatuses);
    }

    @Transactional
    public SeatReservation reserve(Long seatId, Long userId, SeatReservationRequest request) {
        Seat seat = seatMapper.selectById(seatId);
        if (seat == null || seat.getStatus() == null || seat.getStatus() == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "座位不可用");
        }

        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "结束时间必须晚于开始时间");
        }

        // 允许5分钟的时间误差（考虑时区转换和网络延迟）
        LocalDateTime minStartTime = LocalDateTime.now().minusMinutes(5);
        if (request.getStartTime().isBefore(minStartTime)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "开始时间不能早于当前时间");
        }

        // 使用分布式锁防止并发抢座
        String lockKey = "lock:seat:" + seatId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked;
        try {
            locked = lock.tryLock(lockWaitMs, lockLeaseMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SERVER_ERROR, "系统繁忙，请稍后再试");
        }

        if (!locked) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "抢座请求过于频繁，请稍后重试");
        }

        try {
            List<String> blockingStatuses = Arrays.asList(SeatReservationStatus.RESERVED, SeatReservationStatus.CHECKED_IN);
            int overlap = reservationMapper.countOverlap(seatId, request.getStartTime(), request.getEndTime(), blockingStatuses);
            
            if (overlap > 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "该时段座位已被占用");
            }

            SeatReservation reservation = new SeatReservation();
            reservation.setSeatId(seatId);
            reservation.setUserId(userId);
            reservation.setStartTime(request.getStartTime());
            reservation.setEndTime(request.getEndTime());
            reservation.setStatus(SeatReservationStatus.RESERVED);
            reservationMapper.insert(reservation);

            sendReservedMessage(userId, seat);
            return reservation;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public List<SeatReservationView> listMy(Long userId) {
        return reservationMapper.selectMyViews(userId);
    }

    @Transactional
    public void cancel(Long reservationId, Long userId) {
        SeatReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "预约记录不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限取消该预约");
        }
        if (!SeatReservationStatus.RESERVED.equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仅预约状态可取消");
        }
        reservationMapper.updateStatus(reservationId, SeatReservationStatus.CANCELED);
    }

    @Transactional
    public void checkIn(Long reservationId, Long userId) {
        SeatReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "预约记录不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限签到该预约");
        }
        if (!SeatReservationStatus.RESERVED.equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仅预约状态可签到");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(reservation.getStartTime().minusMinutes(30))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "签到时间未到，请在预约时间前30分钟内签到");
        }
        if (now.isAfter(reservation.getEndTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "预约已过期");
        }

        reservationMapper.updateCheckIn(reservationId, now);
    }

    private void sendReservedMessage(Long userId, Seat seat) {
        StudyRoom room = studyRoomMapper.selectById(seat.getStudyRoomId());
        if (room == null) return;
        
        Map<String, String> params = new HashMap<>();
        params.put("room", room.getName());
        params.put("seat", seat.getSeatNo());
        messageService.sendTemplate(userId, "SEAT_RESERVED", params);
    }
}
