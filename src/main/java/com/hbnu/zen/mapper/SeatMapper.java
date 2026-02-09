package com.hbnu.zen.mapper;

import com.hbnu.zen.dto.SeatView;
import com.hbnu.zen.mybatis.entity.Seat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SeatMapper {
    List<Seat> selectByRoomId(Long roomId);

    Seat selectById(Long id);

    List<SeatView> selectAvailableSeats(@Param("roomId") Long roomId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime,
                                        @Param("blockingStatuses") List<String> blockingStatuses);

    void insert(Seat seat);

    void update(Seat seat);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
