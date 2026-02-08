package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EquipmentMapper {
    int insert(Equipment equipment);

    int update(Equipment equipment);

    Equipment selectById(@Param("id") Long id);

    List<Equipment> selectAll(@Param("category") String category,
                              @Param("keyword") String keyword,
                              @Param("availableOnly") Boolean availableOnly);

    int countAll();
}
