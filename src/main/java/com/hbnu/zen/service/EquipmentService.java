package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.mapper.EquipmentMapper;
import com.hbnu.zen.mybatis.entity.Equipment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {
    private final EquipmentMapper equipmentMapper;

    public EquipmentService(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    public Equipment create(Equipment equipment) {
        if (equipment.getStatus() == null) {
            equipment.setStatus(1);
        }
        equipmentMapper.insert(equipment);
        return equipment;
    }

    public Equipment update(Equipment equipment) {
        Equipment existing = equipmentMapper.selectById(equipment.getId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "设备不存在");
        }
        if (equipment.getStatus() == null) {
            equipment.setStatus(existing.getStatus());
        }
        equipmentMapper.update(equipment);
        return equipmentMapper.selectById(equipment.getId());
    }

    public Equipment getById(Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "设备不存在");
        }
        return equipment;
    }

    public List<Equipment> list(String category, String keyword, Boolean availableOnly) {
        return equipmentMapper.selectAll(category, keyword, availableOnly);
    }
}
