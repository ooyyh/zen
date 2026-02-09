package com.hbnu.zen.mapper;

import com.hbnu.zen.dto.StudyRoomView;
import com.hbnu.zen.mybatis.entity.StudyRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StudyRoomMapper {
    List<StudyRoom> selectAll();

    StudyRoom selectById(Long id);

    List<StudyRoomView> selectAvailableViews(@Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);

    void insert(StudyRoom room);

    void update(StudyRoom room);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
