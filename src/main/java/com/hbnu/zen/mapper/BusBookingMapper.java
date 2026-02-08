package com.hbnu.zen.mapper;

import com.hbnu.zen.dto.BusBookingView;
import com.hbnu.zen.mybatis.entity.BusBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BusBookingMapper {
    int insert(BusBooking booking);

    BusBooking selectById(@Param("id") Long id);

    BusBooking selectByTripAndUser(@Param("tripId") Long tripId,
                                   @Param("userId") Long userId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") String status);

    int countBooked(@Param("tripId") Long tripId,
                    @Param("status") String status);

    BusBooking selectEarliestWaitlist(@Param("tripId") Long tripId,
                                      @Param("status") String status);

    List<BusBookingView> selectMyViews(@Param("userId") Long userId);

    List<BusBookingView> selectAllViews(@Param("status") String status);
}
