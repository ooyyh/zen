package com.hbnu.zen.mapper;

import com.hbnu.zen.dto.SeatReservationView;
import com.hbnu.zen.mybatis.entity.SeatReservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SeatReservationMapper {
    void insert(SeatReservation reservation);

    SeatReservation selectById(Long id);

    List<SeatReservationView> selectMyViews(Long userId);

    List<SeatReservationView> selectAllViews();

    int countOverlap(@Param("seatId") Long seatId,
                     @Param("startTime") LocalDateTime startTime,
                     @Param("endTime") LocalDateTime endTime,
                     @Param("blockingStatuses") List<String> blockingStatuses);

    void updateStatus(@Param("id") Long id, @Param("status") String status);

    void updateCheckIn(@Param("id") Long id, @Param("checkInAt") LocalDateTime checkInAt);
}
