package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.common.SeatReservationStatus;
import com.hbnu.zen.dto.SeatReservationRequest;
import com.hbnu.zen.dto.SeatReservationView;
import com.hbnu.zen.dto.SeatView;
import com.hbnu.zen.dto.SeatStatusMessage;
import com.hbnu.zen.mapper.SeatMapper;
import com.hbnu.zen.mapper.SeatReservationMapper;
import com.hbnu.zen.mapper.StudyRoomMapper;
import com.hbnu.zen.mybatis.entity.Seat;
import com.hbnu.zen.mybatis.entity.SeatReservation;
import com.hbnu.zen.mybatis.entity.StudyRoom;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final com.hbnu.zen.service.UserService userService;
    private final long lockWaitMs;
    private final long lockLeaseMs;

    public SeatReservationService(SeatMapper seatMapper,
                                  SeatReservationMapper reservationMapper,
                                  StudyRoomMapper studyRoomMapper,
                                  MessageService messageService,
                                  RedissonClient redissonClient,
                                  SimpMessagingTemplate messagingTemplate,
                                  com.hbnu.zen.service.UserService userService,
                                  @Value("${app.seat.lock-wait-ms:800}") long lockWaitMs,
                                  @Value("${app.seat.lock-lease-ms:5000}") long lockLeaseMs) {
        this.seatMapper = seatMapper;
        this.reservationMapper = reservationMapper;
        this.studyRoomMapper = studyRoomMapper;
        this.messageService = messageService;
        this.redissonClient = redissonClient;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.lockWaitMs = lockWaitMs;
        this.lockLeaseMs = lockLeaseMs;
    }

    public List<SeatView> listAvailableSeats(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<String> blockingStatuses = Arrays.asList(SeatReservationStatus.RESERVED, SeatReservationStatus.CHECKED_IN);
        return seatMapper.selectAvailableSeats(roomId, startTime, endTime, blockingStatuses);
    }

    @Transactional
    public SeatReservation reserve(Long seatId, Long userId, SeatReservationRequest request) {
        System.out.println("=== Reserve Start ===");
        System.out.println("seatId: " + seatId);
        System.out.println("userId: " + userId);
        System.out.println("startTime: " + request.getStartTime());
        System.out.println("endTime: " + request.getEndTime());
        
        try {
            Seat seat = seatMapper.selectById(seatId);
            System.out.println("Seat from DB: " + seat);
            
            if (seat == null || seat.getStatus() == null || seat.getStatus() == 0) {
                System.out.println("Seat not available - throwing exception");
                throw new BusinessException(ErrorCode.NOT_FOUND, "Seat not available");
            }

            if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
                System.out.println("Time validation failed");
                throw new BusinessException(ErrorCode.BAD_REQUEST, "End time must be after start time");
            }

            // 使用分布式锁防止并发抢座
            String lockKey = "lock:seat:" + seatId;
            RLock lock = redissonClient.getLock(lockKey);
            boolean locked;
            try {
                locked = lock.tryLock(lockWaitMs, lockLeaseMs, TimeUnit.MILLISECONDS);
                System.out.println("Lock acquired: " + locked);
            } catch (InterruptedException ex) {
                System.out.println("Lock interrupted: " + ex.getMessage());
                ex.printStackTrace();
                Thread.currentThread().interrupt();
                throw new BusinessException(ErrorCode.SERVER_ERROR, "System busy, please try again later");
            }

            if (!locked) {
                System.out.println("Failed to acquire lock");
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Too many requests, please try again later");
            }

            try {
                List<String> blockingStatuses = Arrays.asList(SeatReservationStatus.RESERVED, SeatReservationStatus.CHECKED_IN);
                int overlap = reservationMapper.countOverlap(seatId, request.getStartTime(), request.getEndTime(), blockingStatuses);
                System.out.println("Overlap count: " + overlap);
                
                if (overlap > 0) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "Seat already reserved for this time");
                }

                SeatReservation reservation = new SeatReservation();
                reservation.setSeatId(seatId);
                reservation.setUserId(userId);
                reservation.setStartTime(request.getStartTime());
                reservation.setEndTime(request.getEndTime());
                reservation.setStatus(SeatReservationStatus.RESERVED);
                
                System.out.println("Inserting reservation: " + reservation);
                reservationMapper.insert(reservation);
                System.out.println("Reservation inserted with id: " + reservation.getId());

                // 广播座位已预约
                broadcastSeatStatus(seat.getId(), "RESERVED", userId);
                
                sendReservedMessage(userId, seat);
                System.out.println("=== Reserve Success ===");
                return reservation;
            } catch (Exception e) {
                System.out.println("=== Exception in try block ===");
                System.out.println("Exception type: " + e.getClass().getName());
                System.out.println("Exception message: " + e.getMessage());
                e.printStackTrace();
                throw e;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    System.out.println("Lock released");
                }
            }
        } catch (BusinessException be) {
            System.out.println("=== BusinessException caught ===");
            System.out.println("Code: " + be.getCode());
            System.out.println("Message: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            System.out.println("=== Unexpected Exception ===");
            System.out.println("Exception type: " + e.getClass().getName());
            System.out.println("Exception message: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    public List<SeatReservationView> listMy(Long userId) {
        return reservationMapper.selectMyViews(userId);
    }

    @Transactional
    public void cancel(Long reservationId, Long userId) {
        SeatReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Reservation not found");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "No permission to cancel this reservation");
        }
        if (!SeatReservationStatus.RESERVED.equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Only reserved status can be canceled");
        }
        reservationMapper.updateStatus(reservationId, SeatReservationStatus.CANCELED);
    }

    @Transactional
    public void checkIn(Long reservationId, Long userId) {
        SeatReservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Reservation not found");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "No permission to check in this reservation");
        }
        if (!SeatReservationStatus.RESERVED.equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Only reserved status can check in");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(reservation.getStartTime().minusMinutes(30))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Check-in time not yet, please check in within 30 minutes before reservation time");
        }
        if (now.isAfter(reservation.getEndTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Reservation expired");
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

    private void broadcastSeatStatus(Long seatId, String status, Long userId) {
        String username = "User" + userId;
        try {
            com.hbnu.zen.mybatis.entity.User user = userService.getById(userId);
            if (user != null && user.getUsername() != null) {
                username = user.getUsername();
            }
        } catch (Exception e) {
            System.out.println("Failed to get username for userId: " + userId);
        }
        
        SeatStatusMessage message = new SeatStatusMessage(seatId, status, userId, username);
        messagingTemplate.convertAndSend("/topic/seat-status", message);
        System.out.println("Broadcast seat status: " + message);
    }

    public void markSeatPending(Long seatId, Long userId) {
        // Store in Redis with 30 second TTL
        String key = "seat:pending:" + seatId;
        redissonClient.getBucket(key).set(userId, 30, TimeUnit.SECONDS);
        System.out.println("Marked seat " + seatId + " as pending for user " + userId + " in Redis");
        broadcastSeatStatus(seatId, "PENDING", userId);
    }

    public void releaseSeatPending(Long seatId, Long userId) {
        // Remove from Redis
        String key = "seat:pending:" + seatId;
        redissonClient.getBucket(key).delete();
        broadcastSeatStatus(seatId, "AVAILABLE", userId);
    }

    public Map<Long, Map<String, Object>> getAllPendingSeats(Long studyRoomId) {
        System.out.println("Getting all pending seats for room: " + studyRoomId);
        Map<Long, Map<String, Object>> result = new HashMap<>();
        
        // Get all seats in this room
        List<Seat> seats = seatMapper.selectByRoomId(studyRoomId);
        System.out.println("Found " + seats.size() + " seats in room");
        
        for (Seat seat : seats) {
            String key = "seat:pending:" + seat.getId();
            Object userIdObj = redissonClient.getBucket(key).get();
            if (userIdObj != null) {
                System.out.println("Found pending seat " + seat.getId() + " for user: " + userIdObj);
                Long userId = (Long) userIdObj;
                String username = "User" + userId;
                try {
                    com.hbnu.zen.mybatis.entity.User user = userService.getById(userId);
                    if (user != null && user.getUsername() != null) {
                        username = user.getUsername();
                    }
                } catch (Exception e) {
                    System.out.println("Failed to get username for userId: " + userId);
                }
                
                Map<String, Object> info = new HashMap<>();
                info.put("userId", userId);
                info.put("username", username);
                result.put(seat.getId(), info);
            }
        }
        
        System.out.println("Returning " + result.size() + " pending seats");
        return result;
    }
}
