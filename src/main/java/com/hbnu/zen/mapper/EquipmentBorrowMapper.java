package com.hbnu.zen.mapper;

import com.hbnu.zen.dto.EquipmentBorrowView;
import com.hbnu.zen.mybatis.entity.EquipmentBorrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EquipmentBorrowMapper {
    int insert(EquipmentBorrow borrow);

    EquipmentBorrow selectById(@Param("id") Long id);

    List<EquipmentBorrow> selectByUserId(@Param("userId") Long userId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") String status);

    int updateDecision(@Param("id") Long id,
                       @Param("status") String status,
                       @Param("approvedBy") Long approvedBy,
                       @Param("remark") String remark,
                       @Param("approvedAt") LocalDateTime approvedAt);

    int updateReturn(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("returnedAt") LocalDateTime returnedAt);

    int countOverlap(@Param("equipmentId") Long equipmentId,
                     @Param("startTime") LocalDateTime startTime,
                     @Param("endTime") LocalDateTime endTime,
                     @Param("statuses") java.util.List<String> statuses);

    int countAll();

    int countByStatus(@Param("status") String status);

    List<EquipmentBorrowView> selectPendingViews(@Param("status") String status);

    List<EquipmentBorrowView> selectMyViews(@Param("userId") Long userId);
}
