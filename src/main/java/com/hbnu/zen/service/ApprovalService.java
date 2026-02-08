package com.hbnu.zen.service;

import com.hbnu.zen.common.ApprovalStatus;
import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.dto.ApprovalView;
import com.hbnu.zen.mapper.ApprovalTaskMapper;
import com.hbnu.zen.mapper.ClassroomMapper;
import com.hbnu.zen.mapper.ReservationMapper;
import com.hbnu.zen.mapper.UserMapper;
import com.hbnu.zen.mybatis.entity.ApprovalTask;
import com.hbnu.zen.mybatis.entity.Classroom;
import com.hbnu.zen.mybatis.entity.Reservation;
import com.hbnu.zen.mybatis.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApprovalService {
    private final ApprovalTaskMapper approvalTaskMapper;
    private final ReservationMapper reservationMapper;
    private final ClassroomMapper classroomMapper;
    private final UserMapper userMapper;

    public ApprovalService(ApprovalTaskMapper approvalTaskMapper,
                           ReservationMapper reservationMapper,
                           ClassroomMapper classroomMapper,
                           UserMapper userMapper) {
        this.approvalTaskMapper = approvalTaskMapper;
        this.reservationMapper = reservationMapper;
        this.classroomMapper = classroomMapper;
        this.userMapper = userMapper;
    }

    public List<ApprovalView> listPendingByRole(String role) {
        List<ApprovalTask> tasks = approvalTaskMapper.selectPending(ApprovalStatus.PENDING, role);
        List<ApprovalView> result = new ArrayList<>();
        for (ApprovalTask task : tasks) {
            Reservation reservation = reservationMapper.selectById(task.getReservationId());
            if (reservation == null) {
                continue;
            }
            Classroom classroom = classroomMapper.selectById(reservation.getClassroomId());
            User user = userMapper.selectById(reservation.getUserId());
            ApprovalView view = new ApprovalView();
            view.setTaskId(task.getId());
            view.setReservationId(reservation.getId());
            view.setUserId(reservation.getUserId());
            view.setUsername(user == null ? "未知" : user.getUsername());
            view.setClassroom(classroom == null ? "未知" : classroom.getBuilding() + " " + classroom.getRoomNo());
            view.setStartTime(reservation.getStartTime());
            view.setEndTime(reservation.getEndTime());
            view.setStatus(task.getStatus());
            view.setRemark(task.getRemark());
            view.setCreatedAt(task.getCreatedAt());
            result.add(view);
        }
        return result;
    }

    public ApprovalTask getTask(Long taskId) {
        ApprovalTask task = approvalTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "审批任务不存在");
        }
        return task;
    }
}
