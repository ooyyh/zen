package com.hbnu.zen.mybatis.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalTask {
    private Long id;
    private Long reservationId;
    private String approverRole;
    private Long approverId;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
