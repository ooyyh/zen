package com.hbnu.zen.dto;

import com.hbnu.zen.mybatis.entity.Lecture;

import java.util.List;

public class HomeOverview {
    private int classroomCount;
    private int equipmentCount;
    private int lectureOpenCount;
    private int busTripOpenCount;
    private int pendingApprovalCount;
    private int reservationApprovedCount;
    private int reservationPendingCount;
    private List<HomeClassroomHot> hotClassrooms;
    private List<Lecture> upcomingLectures;

    public int getClassroomCount() {
        return classroomCount;
    }

    public void setClassroomCount(int classroomCount) {
        this.classroomCount = classroomCount;
    }

    public int getEquipmentCount() {
        return equipmentCount;
    }

    public void setEquipmentCount(int equipmentCount) {
        this.equipmentCount = equipmentCount;
    }

    public int getLectureOpenCount() {
        return lectureOpenCount;
    }

    public void setLectureOpenCount(int lectureOpenCount) {
        this.lectureOpenCount = lectureOpenCount;
    }

    public int getBusTripOpenCount() {
        return busTripOpenCount;
    }

    public void setBusTripOpenCount(int busTripOpenCount) {
        this.busTripOpenCount = busTripOpenCount;
    }

    public int getPendingApprovalCount() {
        return pendingApprovalCount;
    }

    public void setPendingApprovalCount(int pendingApprovalCount) {
        this.pendingApprovalCount = pendingApprovalCount;
    }

    public int getReservationApprovedCount() {
        return reservationApprovedCount;
    }

    public void setReservationApprovedCount(int reservationApprovedCount) {
        this.reservationApprovedCount = reservationApprovedCount;
    }

    public int getReservationPendingCount() {
        return reservationPendingCount;
    }

    public void setReservationPendingCount(int reservationPendingCount) {
        this.reservationPendingCount = reservationPendingCount;
    }

    public List<HomeClassroomHot> getHotClassrooms() {
        return hotClassrooms;
    }

    public void setHotClassrooms(List<HomeClassroomHot> hotClassrooms) {
        this.hotClassrooms = hotClassrooms;
    }

    public List<Lecture> getUpcomingLectures() {
        return upcomingLectures;
    }

    public void setUpcomingLectures(List<Lecture> upcomingLectures) {
        this.upcomingLectures = upcomingLectures;
    }
}
