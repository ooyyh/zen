package com.hbnu.zen.mapper;

import com.hbnu.zen.dto.LectureCheckinView;
import com.hbnu.zen.mybatis.entity.LectureCheckin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LectureCheckinMapper {
    int insert(LectureCheckin checkin);

    LectureCheckin selectByLectureAndUser(@Param("lectureId") Long lectureId,
                                          @Param("userId") Long userId);

    List<LectureCheckinView> selectViewsByLecture(@Param("lectureId") Long lectureId);
}
