package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.dto.EquipmentBorrowView;
import com.hbnu.zen.dto.EquipmentCreateRequest;
import com.hbnu.zen.dto.EquipmentDecisionRequest;
import com.hbnu.zen.mybatis.entity.Equipment;
import com.hbnu.zen.service.EquipmentBorrowService;
import com.hbnu.zen.service.EquipmentService;
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
@RequestMapping("/api/admin/equipments")
public class AdminEquipmentController {
    private final EquipmentService equipmentService;
    private final EquipmentBorrowService borrowService;

    public AdminEquipmentController(EquipmentService equipmentService, EquipmentBorrowService borrowService) {
        this.equipmentService = equipmentService;
        this.borrowService = borrowService;
    }

    @GetMapping
    public ApiResponse<List<Equipment>> list(@RequestParam(required = false) String category,
                                             @RequestParam(required = false) String keyword) {
        AuthUtil.requireRole(Role.ADMIN);
        return ApiResponse.success(equipmentService.list(category, keyword, null));
    }

    @PostMapping
    public ApiResponse<Equipment> create(@Validated @RequestBody EquipmentCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        Equipment equipment = new Equipment();
        equipment.setName(request.getName());
        equipment.setCategory(request.getCategory());
        equipment.setAssetNo(request.getAssetNo());
        equipment.setLocation(request.getLocation());
        equipment.setTotalQty(request.getTotalQty());
        equipment.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        return ApiResponse.success(equipmentService.create(equipment));
    }

    @PutMapping("/{id}")
    public ApiResponse<Equipment> update(@PathVariable Long id,
                                         @Validated @RequestBody EquipmentCreateRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setName(request.getName());
        equipment.setCategory(request.getCategory());
        equipment.setAssetNo(request.getAssetNo());
        equipment.setLocation(request.getLocation());
        equipment.setTotalQty(request.getTotalQty());
        equipment.setStatus(request.getStatus());
        return ApiResponse.success(equipmentService.update(equipment));
    }

    @GetMapping("/borrows")
    public ApiResponse<List<EquipmentBorrowView>> listPending(@RequestParam(required = false) String status) {
        AuthUtil.requireRole(Role.ADMIN);
        if (status != null && !status.isEmpty()) {
            return ApiResponse.success(borrowService.listPending());
        }
        return ApiResponse.success(borrowService.listPending());
    }

    @PostMapping("/borrows/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id,
                                     @RequestBody(required = false) EquipmentDecisionRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        String remark = request == null ? null : request.getRemark();
        borrowService.approve(id, AuthUtil.getUserId(), remark);
        return ApiResponse.success();
    }

    @PostMapping("/borrows/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id,
                                    @RequestBody(required = false) EquipmentDecisionRequest request) {
        AuthUtil.requireRole(Role.ADMIN);
        String remark = request == null ? null : request.getRemark();
        borrowService.reject(id, AuthUtil.getUserId(), remark);
        return ApiResponse.success();
    }
}
