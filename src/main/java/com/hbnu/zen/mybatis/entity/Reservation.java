package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reservation {
    private Long id;
    private Long userId;
    private Long classroomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer approvalRequired;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
