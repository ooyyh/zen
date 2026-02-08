package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationMapper {
    int insert(Reservation reservation);

    Reservation selectById(@Param("id") Long id);

    List<Reservation> selectByUserId(@Param("userId") Long userId);

    int countConflicts(@Param("classroomId") Long classroomId,
                       @Param("startTime") LocalDateTime startTime,
                       @Param("endTime") LocalDateTime endTime,
                       @Param("statuses") List<String> statuses);

    int countUserDaily(@Param("userId") Long userId,
                       @Param("dayStart") LocalDateTime dayStart,
                       @Param("dayEnd") LocalDateTime dayEnd,
                       @Param("statuses") List<String> statuses);

    int updateStatus(@Param("id") Long id,
                     @Param("status") String status);

    int countAll();

    int countByStatus(@Param("status") String status);
}
