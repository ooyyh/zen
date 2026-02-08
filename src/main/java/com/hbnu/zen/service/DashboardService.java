package com.hbnu.zen.service;

import com.hbnu.zen.common.ApprovalStatus;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.DashboardSummary;
import com.hbnu.zen.mapper.ApprovalTaskMapper;
import com.hbnu.zen.mapper.ClassroomMapper;
import com.hbnu.zen.mapper.MessageMapper;
import com.hbnu.zen.mapper.ReservationMapper;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final ClassroomMapper classroomMapper;
    private final ReservationMapper reservationMapper;
    private final ApprovalTaskMapper approvalTaskMapper;
    private final MessageMapper messageMapper;

    public DashboardService(ClassroomMapper classroomMapper,
                            ReservationMapper reservationMapper,
                            ApprovalTaskMapper approvalTaskMapper,
                            MessageMapper messageMapper) {
        this.classroomMapper = classroomMapper;
        this.reservationMapper = reservationMapper;
        this.approvalTaskMapper = approvalTaskMapper;
        this.messageMapper = messageMapper;
    }

    public DashboardSummary getSummary(Long userId) {
        DashboardSummary summary = new DashboardSummary();
        summary.setClassroomCount(classroomMapper.countAll());
        summary.setReservationCount(reservationMapper.countAll());
        summary.setPendingApprovalCount(approvalTaskMapper.countPending(ApprovalStatus.PENDING, Role.ADMIN));
        summary.setUnreadMessageCount(messageMapper.countUnread(userId));
        return summary;
    }
}
