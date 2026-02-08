package com.hbnu.zen.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateReservationRequest {
    @NotNull(message = "教室不能为空")
    private Long classroomId;
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    private String reason;

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
