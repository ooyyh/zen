package com.hbnu.zen.service;

import com.hbnu.zen.common.ApprovalStatus;
import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.common.ReservationStatus;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.CreateReservationRequest;
import com.hbnu.zen.dto.ReservationRules;
import com.hbnu.zen.mapper.ApprovalTaskMapper;
import com.hbnu.zen.mapper.ClassroomMapper;
import com.hbnu.zen.mapper.ReservationMapper;
import com.hbnu.zen.mybatis.entity.ApprovalTask;
import com.hbnu.zen.mybatis.entity.Classroom;
import com.hbnu.zen.mybatis.entity.Reservation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationService {
    private final ReservationMapper reservationMapper;
    private final ClassroomMapper classroomMapper;
    private final ApprovalTaskMapper approvalTaskMapper;
    private final ConfigService configService;
    private final MessageService messageService;
    private final RedissonClient redissonClient;

    private final long lockWaitMs;
    private final long lockLeaseMs;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReservationService(ReservationMapper reservationMapper,
                              ClassroomMapper classroomMapper,
                              ApprovalTaskMapper approvalTaskMapper,
                              ConfigService configService,
                              MessageService messageService,
                              RedissonClient redissonClient,
                              @Value("${app.reservation.lock-wait-ms}") long lockWaitMs,
                              @Value("${app.reservation.lock-lease-ms}") long lockLeaseMs) {
        this.reservationMapper = reservationMapper;
        this.classroomMapper = classroomMapper;
        this.approvalTaskMapper = approvalTaskMapper;
        this.configService = configService;
        this.messageService = messageService;
        this.redissonClient = redissonClient;
        this.lockWaitMs = lockWaitMs;
        this.lockLeaseMs = lockLeaseMs;
    }

    @Transactional
    public Reservation createReservation(Long userId, CreateReservationRequest request) {
        Classroom classroom = classroomMapper.selectById(request.getClassroomId());
        if (classroom == null || classroom.getStatus() == null || classroom.getStatus() == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教室不可用");
        }
        ReservationRules rules = configService.getReservationRules();
        validateRules(request, rules);

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();
        List<String> blockingStatuses = Arrays.asList(ReservationStatus.PENDING_APPROVAL, ReservationStatus.APPROVED);
        LocalDate day = startTime.toLocalDate();
        int dailyCount = reservationMapper.countUserDaily(userId, day.atStartOfDay(), day.plusDays(1).atStartOfDay(), blockingStatuses);
        if (rules.getDailyLimit() != null && dailyCount >= rules.getDailyLimit()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已超过每日预约上限");
        }

        String lockKey = "lock:classroom:" + request.getClassroomId();
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked;
        try {
            locked = lock.tryLock(lockWaitMs, lockLeaseMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SERVER_ERROR, "系统繁忙，请稍后再试");
        }
        if (!locked) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "预约请求过于频繁，请稍后重试");
        }
        try {
            int conflicts = reservationMapper.countConflicts(request.getClassroomId(), startTime, endTime, blockingStatuses);
            if (conflicts > 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "该时段已被预约");
            }
            Reservation reservation = new Reservation();
            reservation.setUserId(userId);
            reservation.setClassroomId(request.getClassroomId());
            reservation.setStartTime(startTime);
            reservation.setEndTime(endTime);
            reservation.setReason(request.getReason());
            boolean approvalRequired = rules.getApprovalRequired() == null || rules.getApprovalRequired();
            reservation.setApprovalRequired(approvalRequired ? 1 : 0);
            reservation.setStatus(approvalRequired ? ReservationStatus.PENDING_APPROVAL : ReservationStatus.APPROVED);
            reservationMapper.insert(reservation);

            if (approvalRequired) {
                ApprovalTask task = new ApprovalTask();
                task.setReservationId(reservation.getId());
                task.setApproverRole(Role.ADMIN);
                task.setStatus(ApprovalStatus.PENDING);
                approvalTaskMapper.insert(task);
                sendCreatedMessage(userId, classroom, startTime, endTime);
            } else {
                sendApprovedMessage(userId, classroom, startTime, endTime);
            }
            return reservation;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public List<Reservation> listByUser(Long userId) {
        return reservationMapper.selectByUserId(userId);
    }

    public Reservation getById(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "预约不存在");
        }
        return reservation;
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId, boolean admin) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "预约不存在");
        }
        if (!admin && !reservation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限取消该预约");
        }
        if (ReservationStatus.CANCELED.equals(reservation.getStatus())) {
            return;
        }
        if (ReservationStatus.REJECTED.equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "预约已被驳回，无法取消");
        }
        reservationMapper.updateStatus(reservationId, ReservationStatus.CANCELED);
    }

    @Transactional
    public void approveReservation(Long reservationId, Long approverId, String remark) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "预约不存在");
        }
        reservationMapper.updateStatus(reservationId, ReservationStatus.APPROVED);
        ApprovalTask task = approvalTaskMapper.selectByReservationId(reservationId);
        if (task != null) {
            approvalTaskMapper.updateDecision(task.getId(), ApprovalStatus.APPROVED, approverId, remark);
        }
        Classroom classroom = classroomMapper.selectById(reservation.getClassroomId());
        sendApprovedMessage(reservation.getUserId(), classroom, reservation.getStartTime(), reservation.getEndTime());
    }

    private void validateRules(CreateReservationRequest request, ReservationRules rules) {
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "结束时间必须大于开始时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "开始时间不能早于当前时间");
        }
        if (rules.getAdvanceDays() != null) {
            LocalDate maxDate = LocalDate.now().plusDays(rules.getAdvanceDays());
            if (startTime.toLocalDate().isAfter(maxDate)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "预约超出可提前天数");
            }
        }
        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        if (rules.getMinDurationMinutes() != null && durationMinutes < rules.getMinDurationMinutes()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "预约时长过短");
        }
        if (rules.getMaxDurationMinutes() != null && durationMinutes > rules.getMaxDurationMinutes()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "预约时长过长");
        }
        if (rules.getTimeSlotMinutes() != null && rules.getTimeSlotMinutes() > 0) {
            int slot = rules.getTimeSlotMinutes();
            if (startTime.getMinute() % slot != 0 || endTime.getMinute() % slot != 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "时间必须按" + slot + "分钟粒度预约");
            }
        }
        if (startTime.toLocalTime().isBefore(LocalTime.of(7, 0)) || endTime.toLocalTime().isAfter(LocalTime.of(23, 0))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "预约时间需在07:00-23:00范围内");
        }
    }

    private void sendCreatedMessage(Long userId, Classroom classroom, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, String> params = new HashMap<>();
        params.put("classroom", formatClassroom(classroom));
        params.put("startTime", startTime.format(TIME_FORMATTER));
        params.put("endTime", endTime.format(TIME_FORMATTER));
        messageService.sendTemplate(userId, "RESERVATION_CREATED", params);
    }

    private void sendApprovedMessage(Long userId, Classroom classroom, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, String> params = new HashMap<>();
        params.put("classroom", formatClassroom(classroom));
        params.put("startTime", startTime.format(TIME_FORMATTER));
        params.put("endTime", endTime.format(TIME_FORMATTER));
        messageService.sendTemplate(userId, "RESERVATION_APPROVED", params);
    }

    @Transactional
    public void rejectReservation(Long reservationId, Long approverId, String remark) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "预约不存在");
        }
        reservationMapper.updateStatus(reservationId, ReservationStatus.REJECTED);
        ApprovalTask task = approvalTaskMapper.selectByReservationId(reservationId);
        if (task != null) {
            approvalTaskMapper.updateDecision(task.getId(), ApprovalStatus.REJECTED, approverId, remark);
        }
        Map<String, String> params = new HashMap<>();
        params.put("remark", remark == null ? "无" : remark);
        messageService.sendTemplate(reservation.getUserId(), "RESERVATION_REJECTED", params);
    }

    private String formatClassroom(Classroom classroom) {
        if (classroom == null) {
            return "未知教室";
        }
        return classroom.getBuilding() + " " + classroom.getRoomNo();
    }
}
