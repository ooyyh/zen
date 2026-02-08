package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.common.LectureSignupStatus;
import com.hbnu.zen.common.LectureStatus;
import com.hbnu.zen.dto.LectureCreateRequest;
import com.hbnu.zen.dto.LectureSignupView;
import com.hbnu.zen.dto.LectureCheckinView;
import com.hbnu.zen.mapper.LectureCheckinMapper;
import com.hbnu.zen.mapper.LectureMapper;
import com.hbnu.zen.mapper.LectureSignupMapper;
import com.hbnu.zen.mybatis.entity.Lecture;
import com.hbnu.zen.mybatis.entity.LectureCheckin;
import com.hbnu.zen.mybatis.entity.LectureSignup;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LectureService {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LectureMapper lectureMapper;
    private final LectureSignupMapper signupMapper;
    private final LectureCheckinMapper checkinMapper;
    private final MessageService messageService;
    private final RedissonClient redissonClient;
    private final long lockWaitMs;
    private final long lockLeaseMs;

    public LectureService(LectureMapper lectureMapper,
                          LectureSignupMapper signupMapper,
                          LectureCheckinMapper checkinMapper,
                          MessageService messageService,
                          RedissonClient redissonClient,
                          @Value("${app.lecture.lock-wait-ms}") long lockWaitMs,
                          @Value("${app.lecture.lock-lease-ms}") long lockLeaseMs) {
        this.lectureMapper = lectureMapper;
        this.signupMapper = signupMapper;
        this.checkinMapper = checkinMapper;
        this.messageService = messageService;
        this.redissonClient = redissonClient;
        this.lockWaitMs = lockWaitMs;
        this.lockLeaseMs = lockLeaseMs;
    }

    public Lecture createLecture(LectureCreateRequest request) {
        validateLectureRequest(request);
        Lecture lecture = new Lecture();
        lecture.setTitle(request.getTitle());
        lecture.setSpeaker(request.getSpeaker());
        lecture.setLocation(request.getLocation());
        lecture.setStartTime(request.getStartTime());
        lecture.setEndTime(request.getEndTime());
        lecture.setCapacity(request.getCapacity());
        lecture.setStatus(request.getStatus() == null ? LectureStatus.DRAFT : request.getStatus());
        lectureMapper.insert(lecture);
        return lecture;
    }

    public Lecture updateLecture(Long id, LectureCreateRequest request) {
        validateLectureRequest(request);
        Lecture existing = lectureMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "讲座不存在");
        }
        Lecture lecture = new Lecture();
        lecture.setId(id);
        lecture.setTitle(request.getTitle());
        lecture.setSpeaker(request.getSpeaker());
        lecture.setLocation(request.getLocation());
        lecture.setStartTime(request.getStartTime());
        lecture.setEndTime(request.getEndTime());
        lecture.setCapacity(request.getCapacity());
        lecture.setStatus(request.getStatus() == null ? existing.getStatus() : request.getStatus());
        lectureMapper.update(lecture);
        return lectureMapper.selectById(id);
    }

    public List<Lecture> listLectures(String status) {
        return lectureMapper.selectAll(status);
    }

    public Lecture getLecture(Long id) {
        Lecture lecture = lectureMapper.selectById(id);
        if (lecture == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "讲座不存在");
        }
        return lecture;
    }

    @Transactional
    public LectureSignup signup(Long lectureId, Long userId) {
        Lecture lecture = getLecture(lectureId);
        if (!LectureStatus.OPEN.equals(lecture.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "讲座未开放报名");
        }
        if (lecture.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "讲座已开始，无法报名");
        }
        String lockKey = "lock:lecture:" + lectureId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked;
        try {
            locked = lock.tryLock(lockWaitMs, lockLeaseMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SERVER_ERROR, "系统繁忙，请稍后再试");
        }
        if (!locked) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "报名请求过于频繁，请稍后重试");
        }
        try {
            LectureSignup existing = signupMapper.selectByLectureAndUser(lectureId, userId);
            if (existing != null && !LectureSignupStatus.CANCELED.equals(existing.getStatus())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "已报名或候补，无需重复报名");
            }
            int signedCount = signupMapper.countByStatus(lectureId, LectureSignupStatus.SIGNED_UP);
            String status = signedCount < lecture.getCapacity() ? LectureSignupStatus.SIGNED_UP : LectureSignupStatus.WAITLIST;
            if (existing != null) {
                signupMapper.updateStatus(existing.getId(), status);
                sendLectureMessage(userId, lecture, status);
                return existing;
            } else {
                LectureSignup signup = new LectureSignup();
                signup.setLectureId(lectureId);
                signup.setUserId(userId);
                signup.setStatus(status);
                signupMapper.insert(signup);
                sendLectureMessage(userId, lecture, status);
                return signup;
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void cancelSignup(Long lectureId, Long userId) {
        LectureSignup signup = signupMapper.selectByLectureAndUser(lectureId, userId);
        if (signup == null || LectureSignupStatus.CANCELED.equals(signup.getStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "报名记录不存在");
        }
        String lockKey = "lock:lecture:" + lectureId;
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
            boolean wasSigned = LectureSignupStatus.SIGNED_UP.equals(signup.getStatus());
            signupMapper.updateStatus(signup.getId(), LectureSignupStatus.CANCELED);
            if (wasSigned) {
                LectureSignup waitlist = signupMapper.selectEarliestWaitlist(lectureId, LectureSignupStatus.WAITLIST);
                if (waitlist != null) {
                    signupMapper.updateStatus(waitlist.getId(), LectureSignupStatus.SIGNED_UP);
                    Lecture lecture = getLecture(lectureId);
                    sendLectureMessage(waitlist.getUserId(), lecture, LectureSignupStatus.SIGNED_UP);
                }
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public List<LectureSignupView> listMySignups(Long userId) {
        return signupMapper.selectMySignups(userId);
    }

    @Transactional
    public void checkIn(Long lectureId, Long userId) {
        Lecture lecture = getLecture(lectureId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = lecture.getStartTime().minusHours(1);
        if (now.isBefore(windowStart) || now.isAfter(lecture.getEndTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不在签到时间范围内");
        }
        LectureSignup signup = signupMapper.selectByLectureAndUser(lectureId, userId);
        if (signup == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "未报名该讲座");
        }
        if (LectureSignupStatus.WAITLIST.equals(signup.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "候补状态无法签到");
        }
        if (LectureSignupStatus.CANCELED.equals(signup.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已取消报名");
        }
        LectureCheckin existing = checkinMapper.selectByLectureAndUser(lectureId, userId);
        if (existing != null || LectureSignupStatus.CHECKED_IN.equals(signup.getStatus())) {
            return;
        }
        LectureCheckin checkin = new LectureCheckin();
        checkin.setLectureId(lectureId);
        checkin.setUserId(userId);
        checkin.setCheckInAt(now);
        checkinMapper.insert(checkin);
        signupMapper.updateStatus(signup.getId(), LectureSignupStatus.CHECKED_IN);
        Map<String, String> params = new HashMap<>();
        params.put("lectureTitle", lecture.getTitle());
        params.put("startTime", lecture.getStartTime().format(TIME_FORMATTER));
        messageService.sendTemplate(userId, "LECTURE_CHECKIN_SUCCESS", params);
    }

    public List<LectureCheckinView> listCheckins(Long lectureId) {
        return checkinMapper.selectViewsByLecture(lectureId);
    }

    private void validateLectureRequest(LectureCreateRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "结束时间必须晚于开始时间");
        }
        if (request.getCapacity() <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "容量必须大于0");
        }
    }

    private void sendLectureMessage(Long userId, Lecture lecture, String status) {
        Map<String, String> params = new HashMap<>();
        params.put("lectureTitle", lecture.getTitle());
        params.put("startTime", lecture.getStartTime().format(TIME_FORMATTER));
        if (LectureSignupStatus.SIGNED_UP.equals(status)) {
            messageService.sendTemplate(userId, "LECTURE_SIGNUP_SUCCESS", params);
        } else {
            messageService.sendTemplate(userId, "LECTURE_WAITLIST", params);
        }
    }
}
