package com.hbnu.zen.service;

import com.hbnu.zen.common.ApprovalStatus;
import com.hbnu.zen.common.BusTripStatus;
import com.hbnu.zen.common.LectureStatus;
import com.hbnu.zen.common.ReservationStatus;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.HomeOverview;
import com.hbnu.zen.mapper.ApprovalTaskMapper;
import com.hbnu.zen.mapper.BusTripMapper;
import com.hbnu.zen.mapper.ClassroomMapper;
import com.hbnu.zen.mapper.EquipmentMapper;
import com.hbnu.zen.mapper.LectureMapper;
import com.hbnu.zen.mapper.ReservationMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HomeService {
    private final ClassroomMapper classroomMapper;
    private final EquipmentMapper equipmentMapper;
    private final LectureMapper lectureMapper;
    private final BusTripMapper busTripMapper;
    private final ReservationMapper reservationMapper;
    private final ApprovalTaskMapper approvalTaskMapper;

    public HomeService(ClassroomMapper classroomMapper,
                       EquipmentMapper equipmentMapper,
                       LectureMapper lectureMapper,
                       BusTripMapper busTripMapper,
                       ReservationMapper reservationMapper,
                       ApprovalTaskMapper approvalTaskMapper) {
        this.classroomMapper = classroomMapper;
        this.equipmentMapper = equipmentMapper;
        this.lectureMapper = lectureMapper;
        this.busTripMapper = busTripMapper;
        this.reservationMapper = reservationMapper;
        this.approvalTaskMapper = approvalTaskMapper;
    }

    public HomeOverview overview() {
        HomeOverview overview = new HomeOverview();
        overview.setClassroomCount(classroomMapper.countAll());
        overview.setEquipmentCount(equipmentMapper.countAll());
        overview.setLectureOpenCount(lectureMapper.countByStatus(LectureStatus.OPEN));
        overview.setBusTripOpenCount(busTripMapper.countByStatus(BusTripStatus.OPEN));
        overview.setPendingApprovalCount(approvalTaskMapper.countPending(ApprovalStatus.PENDING, Role.ADMIN));
        overview.setReservationApprovedCount(reservationMapper.countByStatus(ReservationStatus.APPROVED));
        overview.setReservationPendingCount(reservationMapper.countByStatus(ReservationStatus.PENDING_APPROVAL));
        overview.setHotClassrooms(reservationMapper.selectHotClassrooms(3));
        overview.setUpcomingLectures(lectureMapper.selectUpcomingOpen(LocalDateTime.now(), 3));
        return overview;
    }
}
