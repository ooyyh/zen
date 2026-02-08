package com.hbnu.zen.dto;

public class DashboardSummary {
    private int classroomCount;
    private int reservationCount;
    private int pendingApprovalCount;
    private int unreadMessageCount;
    private int equipmentCount;
    private int busTripCount;

    public int getClassroomCount() {
        return classroomCount;
    }

    public void setClassroomCount(int classroomCount) {
        this.classroomCount = classroomCount;
    }

    public int getReservationCount() {
        return reservationCount;
    }

    public void setReservationCount(int reservationCount) {
        this.reservationCount = reservationCount;
    }

    public int getPendingApprovalCount() {
        return pendingApprovalCount;
    }

    public void setPendingApprovalCount(int pendingApprovalCount) {
        this.pendingApprovalCount = pendingApprovalCount;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public int getEquipmentCount() {
        return equipmentCount;
    }

    public void setEquipmentCount(int equipmentCount) {
        this.equipmentCount = equipmentCount;
    }

    public int getBusTripCount() {
        return busTripCount;
    }

    public void setBusTripCount(int busTripCount) {
        this.busTripCount = busTripCount;
    }
}
