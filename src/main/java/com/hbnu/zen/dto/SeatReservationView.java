package com.hbnu.zen.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeatReservationView {
    private Long id;
    private Long seatId;
    private String seatNo;
    private String studyRoomName;
    private String building;
    private Integer floor;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime checkInAt;
}
