package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeatReservation {
    private Long id;
    private Long seatId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime checkInAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
