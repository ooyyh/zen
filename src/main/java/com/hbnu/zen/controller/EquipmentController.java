package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.AuthUtil;
import com.hbnu.zen.dto.EquipmentBorrowRequest;
import com.hbnu.zen.dto.EquipmentBorrowView;
import com.hbnu.zen.mybatis.entity.Equipment;
import com.hbnu.zen.mybatis.entity.EquipmentBorrow;
import com.hbnu.zen.service.EquipmentBorrowService;
import com.hbnu.zen.service.EquipmentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {
    private final EquipmentService equipmentService;
    private final EquipmentBorrowService borrowService;

    public EquipmentController(EquipmentService equipmentService, EquipmentBorrowService borrowService) {
        this.equipmentService = equipmentService;
        this.borrowService = borrowService;
    }

    @GetMapping
    public ApiResponse<List<Equipment>> list(@RequestParam(required = false) String category,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Boolean availableOnly) {
        return ApiResponse.success(equipmentService.list(category, keyword, availableOnly));
    }

    @GetMapping("/{id}")
    public ApiResponse<Equipment> detail(@PathVariable Long id) {
        return ApiResponse.success(equipmentService.getById(id));
    }

    @PostMapping("/{id}/borrow")
    public ApiResponse<EquipmentBorrow> borrow(@PathVariable Long id,
                                               @Validated @RequestBody EquipmentBorrowRequest request) {
        return ApiResponse.success(borrowService.createBorrow(id, AuthUtil.getUserId(), request));
    }

    @PostMapping("/borrows/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        borrowService.cancel(id, AuthUtil.getUserId());
        return ApiResponse.success();
    }

    @PostMapping("/borrows/{id}/return")
    public ApiResponse<Void> returnEquipment(@PathVariable Long id) {
        borrowService.returnEquipment(id, AuthUtil.getUserId());
        return ApiResponse.success();
    }

    @GetMapping("/borrows/my")
    public ApiResponse<List<EquipmentBorrowView>> my() {
        return ApiResponse.success(borrowService.listMy(AuthUtil.getUserId()));
    }
}
