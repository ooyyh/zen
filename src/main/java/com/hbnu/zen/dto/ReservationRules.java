package com.hbnu.zen.dto;

public class ReservationRules {
    private Integer timeSlotMinutes;
    private Integer advanceDays;
    private Integer dailyLimit;
    private Integer minDurationMinutes;
    private Integer maxDurationMinutes;
    private Boolean approvalRequired;

    public Integer getTimeSlotMinutes() {
        return timeSlotMinutes;
    }

    public void setTimeSlotMinutes(Integer timeSlotMinutes) {
        this.timeSlotMinutes = timeSlotMinutes;
    }

    public Integer getAdvanceDays() {
        return advanceDays;
    }

    public void setAdvanceDays(Integer advanceDays) {
        this.advanceDays = advanceDays;
    }

    public Integer getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Integer dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public Integer getMinDurationMinutes() {
        return minDurationMinutes;
    }

    public void setMinDurationMinutes(Integer minDurationMinutes) {
        this.minDurationMinutes = minDurationMinutes;
    }

    public Integer getMaxDurationMinutes() {
        return maxDurationMinutes;
    }

    public void setMaxDurationMinutes(Integer maxDurationMinutes) {
        this.maxDurationMinutes = maxDurationMinutes;
    }

    public Boolean getApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(Boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }
}
