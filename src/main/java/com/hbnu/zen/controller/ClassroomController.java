package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.ClassroomCreateRequest;
import com.hbnu.zen.mybatis.entity.Classroom;
import com.hbnu.zen.service.ClassroomService;
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
@RequestMapping("/api")
public class ClassroomController {
    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping("/classrooms")
    public ApiResponse<List<Classroom>> list(@RequestParam(required = false) String building,
                                             @RequestParam(required = false) Integer minCapacity) {
        return ApiResponse.success(classroomService.list(building, minCapacity));
    }

    @GetMapping("/classrooms/{id}")
    public ApiResponse<Classroom> detail(@PathVariable Long id) {
        return ApiResponse.success(classroomService.getById(id));
    }

    @PostMapping("/admin/classrooms")
    public ApiResponse<Classroom> create(@Validated @RequestBody ClassroomCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        Classroom classroom = new Classroom();
        classroom.setBuilding(request.getBuilding());
        classroom.setRoomNo(request.getRoomNo());
        classroom.setCapacity(request.getCapacity());
        classroom.setLocation(request.getLocation());
        classroom.setEquipmentJson(request.getEquipmentJson());
        classroom.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        return ApiResponse.success(classroomService.create(classroom));
    }

    @PutMapping("/admin/classrooms/{id}")
    public ApiResponse<Classroom> update(@PathVariable Long id,
                                         @Validated @RequestBody ClassroomCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        Classroom classroom = new Classroom();
        classroom.setId(id);
        classroom.setBuilding(request.getBuilding());
        classroom.setRoomNo(request.getRoomNo());
        classroom.setCapacity(request.getCapacity());
        classroom.setLocation(request.getLocation());
        classroom.setEquipmentJson(request.getEquipmentJson());
        classroom.setStatus(request.getStatus());
        return ApiResponse.success(classroomService.update(classroom));
    }
}
