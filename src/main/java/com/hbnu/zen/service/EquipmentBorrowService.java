package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.EquipmentBorrowStatus;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.dto.EquipmentBorrowRequest;
import com.hbnu.zen.dto.EquipmentBorrowView;
import com.hbnu.zen.mapper.EquipmentBorrowMapper;
import com.hbnu.zen.mapper.EquipmentMapper;
import com.hbnu.zen.mybatis.entity.Equipment;
import com.hbnu.zen.mybatis.entity.EquipmentBorrow;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class EquipmentBorrowService {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final EquipmentMapper equipmentMapper;
    private final EquipmentBorrowMapper borrowMapper;
    private final MessageService messageService;
    private final RedissonClient redissonClient;
    private final long lockWaitMs;
    private final long lockLeaseMs;

    public EquipmentBorrowService(EquipmentMapper equipmentMapper,
                                  EquipmentBorrowMapper borrowMapper,
                                  MessageService messageService,
                                  RedissonClient redissonClient,
                                  @Value("${app.equipment.lock-wait-ms}") long lockWaitMs,
                                  @Value("${app.equipment.lock-lease-ms}") long lockLeaseMs) {
        this.equipmentMapper = equipmentMapper;
        this.borrowMapper = borrowMapper;
        this.messageService = messageService;
        this.redissonClient = redissonClient;
        this.lockWaitMs = lockWaitMs;
        this.lockLeaseMs = lockLeaseMs;
    }

    @Transactional
    public EquipmentBorrow createBorrow(Long equipmentId, Long userId, EquipmentBorrowRequest request) {
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null || equipment.getStatus() == null || equipment.getStatus() == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "设备不可用");
        }
        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "结束时间必须晚于开始时间");
        }
        // 允许5分钟的时间误差（考虑时区转换和网络延迟）
        LocalDateTime minStartTime = LocalDateTime.now().minusMinutes(5);
        if (request.getStartTime().isBefore(minStartTime)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "开始时间不能早于当前时间");
        }

        String lockKey = "lock:equipment:" + equipmentId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked;
        try {
            locked = lock.tryLock(lockWaitMs, lockLeaseMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SERVER_ERROR, "系统繁忙，请稍后再试");
        }
        if (!locked) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "借用请求过于频繁，请稍后重试");
        }
        try {
            List<String> blocking = Arrays.asList(EquipmentBorrowStatus.PENDING, EquipmentBorrowStatus.APPROVED);
            int overlap = borrowMapper.countOverlap(equipmentId, request.getStartTime(), request.getEndTime(), blocking);
            if (overlap >= equipment.getTotalQty()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "该时段设备已被占用");
            }
            EquipmentBorrow borrow = new EquipmentBorrow();
            borrow.setEquipmentId(equipmentId);
            borrow.setUserId(userId);
            borrow.setStartTime(request.getStartTime());
            borrow.setEndTime(request.getEndTime());
            borrow.setReason(request.getReason());
            borrow.setStatus(EquipmentBorrowStatus.PENDING);
            borrowMapper.insert(borrow);
            sendCreatedMessage(userId, equipment, request.getStartTime(), request.getEndTime());
            return borrow;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public List<EquipmentBorrowView> listMy(Long userId) {
        return borrowMapper.selectMyViews(userId);
    }

    public List<EquipmentBorrowView> listPending() {
        return borrowMapper.selectPendingViews(EquipmentBorrowStatus.PENDING);
    }

    @Transactional
    public void approve(Long borrowId, Long approverId, String remark) {
        EquipmentBorrow borrow = borrowMapper.selectById(borrowId);
        if (borrow == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "借用记录不存在");
        }
        Equipment equipment = equipmentMapper.selectById(borrow.getEquipmentId());
        if (equipment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "设备不存在");
        }
        String lockKey = "lock:equipment:" + borrow.getEquipmentId();
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked;
        try {
            locked = lock.tryLock(lockWaitMs, lockLeaseMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SERVER_ERROR, "系统繁忙，请稍后再试");
        }
        if (!locked) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "操作过于频繁，请稍后重试");
        }
        try {
            List<String> blocking = Arrays.asList(EquipmentBorrowStatus.APPROVED);
            int overlap = borrowMapper.countOverlap(borrow.getEquipmentId(), borrow.getStartTime(), borrow.getEndTime(), blocking);
            if (overlap >= equipment.getTotalQty()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "设备库存不足，无法批准");
            }
            borrowMapper.updateDecision(borrowId, EquipmentBorrowStatus.APPROVED, approverId, remark, LocalDateTime.now());
            sendApprovedMessage(borrow.getUserId(), equipment, borrow.getStartTime(), borrow.getEndTime());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void reject(Long borrowId, Long approverId, String remark) {
        EquipmentBorrow borrow = borrowMapper.selectById(borrowId);
        if (borrow == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "借用记录不存在");
        }
        borrowMapper.updateDecision(borrowId, EquipmentBorrowStatus.REJECTED, approverId, remark, LocalDateTime.now());
        Map<String, String> params = new HashMap<>();
        params.put("equipment", buildEquipmentName(equipmentMapper.selectById(borrow.getEquipmentId())));
        params.put("remark", remark == null ? "无" : remark);
        messageService.sendTemplate(borrow.getUserId(), "EQUIPMENT_BORROW_REJECTED", params);
    }

    @Transactional
    public void cancel(Long borrowId, Long userId) {
        EquipmentBorrow borrow = borrowMapper.selectById(borrowId);
        if (borrow == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "借用记录不存在");
        }
        if (!borrow.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限取消该借用");
        }
        if (!EquipmentBorrowStatus.PENDING.equals(borrow.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仅待审批状态可取消");
        }
        borrowMapper.updateStatus(borrowId, EquipmentBorrowStatus.CANCELED);
    }

    @Transactional
    public void returnEquipment(Long borrowId, Long userId) {
        EquipmentBorrow borrow = borrowMapper.selectById(borrowId);
        if (borrow == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "借用记录不存在");
        }
        if (!borrow.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限归还该设备");
        }
        if (!EquipmentBorrowStatus.APPROVED.equals(borrow.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "仅已批准状态可归还");
        }
        borrowMapper.updateReturn(borrowId, EquipmentBorrowStatus.RETURNED, LocalDateTime.now());
        Map<String, String> params = new HashMap<>();
        params.put("equipment", buildEquipmentName(equipmentMapper.selectById(borrow.getEquipmentId())));
        messageService.sendTemplate(borrow.getUserId(), "EQUIPMENT_BORROW_RETURNED", params);
    }

    private void sendCreatedMessage(Long userId, Equipment equipment, LocalDateTime start, LocalDateTime end) {
        Map<String, String> params = new HashMap<>();
        params.put("equipment", buildEquipmentName(equipment));
        params.put("startTime", start.format(TIME_FORMATTER));
        params.put("endTime", end.format(TIME_FORMATTER));
        messageService.sendTemplate(userId, "EQUIPMENT_BORROW_CREATED", params);
    }

    private void sendApprovedMessage(Long userId, Equipment equipment, LocalDateTime start, LocalDateTime end) {
        Map<String, String> params = new HashMap<>();
        params.put("equipment", buildEquipmentName(equipment));
        params.put("startTime", start.format(TIME_FORMATTER));
        params.put("endTime", end.format(TIME_FORMATTER));
        messageService.sendTemplate(userId, "EQUIPMENT_BORROW_APPROVED", params);
    }

    private String buildEquipmentName(Equipment equipment) {
        if (equipment == null) {
            return "未知设备";
        }
        if (equipment.getAssetNo() != null && !equipment.getAssetNo().isEmpty()) {
            return equipment.getName() + "(" + equipment.getAssetNo() + ")";
        }
        return equipment.getName();
    }
}
