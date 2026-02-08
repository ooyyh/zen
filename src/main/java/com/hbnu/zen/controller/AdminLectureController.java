package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.LectureCreateRequest;
import com.hbnu.zen.dto.LectureCheckinView;
import com.hbnu.zen.mybatis.entity.Lecture;
import com.hbnu.zen.service.LectureService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lectures")
public class AdminLectureController {
    private final LectureService lectureService;

    public AdminLectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping
    public ApiResponse<List<Lecture>> list(@RequestParam(required = false) String status) {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(lectureService.listLectures(status));
    }

    @PostMapping
    public ApiResponse<Lecture> create(@Validated @RequestBody LectureCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(lectureService.createLecture(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Lecture> update(@PathVariable Long id,
                                       @Validated @RequestBody LectureCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(lectureService.updateLecture(id, request));
    }

    @GetMapping("/{id}/checkins")
    public ApiResponse<List<LectureCheckinView>> checkins(@PathVariable Long id) {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(lectureService.listCheckins(id));
    }
}
