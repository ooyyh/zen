package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BusTrip {
    private Long id;
    private Long routeId;
    private String busNo;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer capacity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
