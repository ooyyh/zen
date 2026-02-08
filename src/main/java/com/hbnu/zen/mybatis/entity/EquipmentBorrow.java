package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentBorrow {
    private Long id;
    private Long equipmentId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String reason;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String remark;
    private LocalDateTime returnedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
