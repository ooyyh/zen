package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.dto.LectureSignupView;
import com.hbnu.zen.mybatis.entity.Lecture;
import com.hbnu.zen.mybatis.entity.LectureSignup;
import com.hbnu.zen.service.LectureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {
    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping
    public ApiResponse<List<Lecture>> list(@RequestParam(required = false) String status) {
        return ApiResponse.success(lectureService.listLectures(status));
    }

    @GetMapping("/{id}")
    public ApiResponse<Lecture> detail(@PathVariable Long id) {
        return ApiResponse.success(lectureService.getLecture(id));
    }

    @PostMapping("/{id}/signup")
    public ApiResponse<LectureSignup> signup(@PathVariable Long id) {
        return ApiResponse.success(lectureService.signup(id, AuthUtil.getUserId()));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        lectureService.cancelSignup(id, AuthUtil.getUserId());
        return ApiResponse.success();
    }

    @PostMapping("/{id}/checkin")
    public ApiResponse<Void> checkin(@PathVariable Long id) {
        lectureService.checkIn(id, AuthUtil.getUserId());
        return ApiResponse.success();
    }

    @GetMapping("/my/signups")
    public ApiResponse<List<LectureSignupView>> mySignups() {
        return ApiResponse.success(lectureService.listMySignups(AuthUtil.getUserId()));
    }
}
