package com.hbnu.zen.dto;

public class ReportOverview {
    private int reservationTotal;
    private int reservationApproved;
    private int reservationPending;
    private int reservationRejected;
    private int equipmentBorrowTotal;
    private int equipmentBorrowApproved;
    private int equipmentBorrowPending;
    private int lectureTotal;
    private int lectureSignupTotal;
    private int lectureCheckinTotal;
    private int busBookingTotal;
    private int busBooked;
    private int busWaitlist;

    public int getReservationTotal() {
        return reservationTotal;
    }

    public void setReservationTotal(int reservationTotal) {
        this.reservationTotal = reservationTotal;
    }

    public int getReservationApproved() {
        return reservationApproved;
    }

    public void setReservationApproved(int reservationApproved) {
        this.reservationApproved = reservationApproved;
    }

    public int getReservationPending() {
        return reservationPending;
    }

    public void setReservationPending(int reservationPending) {
        this.reservationPending = reservationPending;
    }

    public int getReservationRejected() {
        return reservationRejected;
    }

    public void setReservationRejected(int reservationRejected) {
        this.reservationRejected = reservationRejected;
    }

    public int getEquipmentBorrowTotal() {
        return equipmentBorrowTotal;
    }

    public void setEquipmentBorrowTotal(int equipmentBorrowTotal) {
        this.equipmentBorrowTotal = equipmentBorrowTotal;
    }

    public int getEquipmentBorrowApproved() {
        return equipmentBorrowApproved;
    }

    public void setEquipmentBorrowApproved(int equipmentBorrowApproved) {
        this.equipmentBorrowApproved = equipmentBorrowApproved;
    }

    public int getEquipmentBorrowPending() {
        return equipmentBorrowPending;
    }

    public void setEquipmentBorrowPending(int equipmentBorrowPending) {
        this.equipmentBorrowPending = equipmentBorrowPending;
    }

    public int getLectureTotal() {
        return lectureTotal;
    }

    public void setLectureTotal(int lectureTotal) {
        this.lectureTotal = lectureTotal;
    }

    public int getLectureSignupTotal() {
        return lectureSignupTotal;
    }

    public void setLectureSignupTotal(int lectureSignupTotal) {
        this.lectureSignupTotal = lectureSignupTotal;
    }

    public int getLectureCheckinTotal() {
        return lectureCheckinTotal;
    }

    public void setLectureCheckinTotal(int lectureCheckinTotal) {
        this.lectureCheckinTotal = lectureCheckinTotal;
    }

    public int getBusBookingTotal() {
        return busBookingTotal;
    }

    public void setBusBookingTotal(int busBookingTotal) {
        this.busBookingTotal = busBookingTotal;
    }

    public int getBusBooked() {
        return busBooked;
    }

    public void setBusBooked(int busBooked) {
        this.busBooked = busBooked;
    }

    public int getBusWaitlist() {
        return busWaitlist;
    }

    public void setBusWaitlist(int busWaitlist) {
        this.busWaitlist = busWaitlist;
    }
}
