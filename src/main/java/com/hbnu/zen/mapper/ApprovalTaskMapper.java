package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.ApprovalTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalTaskMapper {
    int insert(ApprovalTask task);

    ApprovalTask selectById(@Param("id") Long id);

    ApprovalTask selectByReservationId(@Param("reservationId") Long reservationId);

    List<ApprovalTask> selectPending(@Param("status") String status,
                                     @Param("role") String role);

    int updateDecision(@Param("id") Long id,
                       @Param("status") String status,
                       @Param("approverId") Long approverId,
                       @Param("remark") String remark);

    int countPending(@Param("status") String status,
                     @Param("role") String role);
}
