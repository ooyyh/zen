package com.hbnu.zen.service;

import com.hbnu.zen.common.BusBookingStatus;
import com.hbnu.zen.common.EquipmentBorrowStatus;
import com.hbnu.zen.common.ReservationStatus;
import com.hbnu.zen.dto.ReportOverview;
import com.hbnu.zen.mapper.BusBookingMapper;
import com.hbnu.zen.mapper.EquipmentBorrowMapper;
import com.hbnu.zen.mapper.LectureCheckinMapper;
import com.hbnu.zen.mapper.LectureMapper;
import com.hbnu.zen.mapper.LectureSignupMapper;
import com.hbnu.zen.mapper.ReservationMapper;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ReservationMapper reservationMapper;
    private final EquipmentBorrowMapper equipmentBorrowMapper;
    private final LectureMapper lectureMapper;
    private final LectureSignupMapper lectureSignupMapper;
    private final LectureCheckinMapper lectureCheckinMapper;
    private final BusBookingMapper busBookingMapper;

    public ReportService(ReservationMapper reservationMapper,
                         EquipmentBorrowMapper equipmentBorrowMapper,
                         LectureMapper lectureMapper,
                         LectureSignupMapper lectureSignupMapper,
                         LectureCheckinMapper lectureCheckinMapper,
                         BusBookingMapper busBookingMapper) {
        this.reservationMapper = reservationMapper;
        this.equipmentBorrowMapper = equipmentBorrowMapper;
        this.lectureMapper = lectureMapper;
        this.lectureSignupMapper = lectureSignupMapper;
        this.lectureCheckinMapper = lectureCheckinMapper;
        this.busBookingMapper = busBookingMapper;
    }

    public ReportOverview overview() {
        ReportOverview overview = new ReportOverview();
        overview.setReservationTotal(reservationMapper.countAll());
        overview.setReservationApproved(reservationMapper.countByStatus(ReservationStatus.APPROVED));
        overview.setReservationPending(reservationMapper.countByStatus(ReservationStatus.PENDING_APPROVAL));
        overview.setReservationRejected(reservationMapper.countByStatus(ReservationStatus.REJECTED));
        overview.setEquipmentBorrowTotal(equipmentBorrowMapper.countAll());
        
        // Count approved + returned as "approved" for report purposes
        int equipmentApproved = equipmentBorrowMapper.countByStatus(EquipmentBorrowStatus.APPROVED);
        int equipmentReturned = equipmentBorrowMapper.countByStatus(EquipmentBorrowStatus.RETURNED);
        overview.setEquipmentBorrowApproved(equipmentApproved + equipmentReturned);
        
        overview.setEquipmentBorrowPending(equipmentBorrowMapper.countByStatus(EquipmentBorrowStatus.PENDING));
        overview.setLectureTotal(lectureMapper.countAll());
        overview.setLectureSignupTotal(lectureSignupMapper.countAll());
        overview.setLectureCheckinTotal(lectureCheckinMapper.countAll());
        overview.setBusBookingTotal(busBookingMapper.countAll());
        overview.setBusBooked(busBookingMapper.countByStatus(BusBookingStatus.BOOKED));
        overview.setBusWaitlist(busBookingMapper.countByStatus(BusBookingStatus.WAITLIST));
        return overview;
    }
}
