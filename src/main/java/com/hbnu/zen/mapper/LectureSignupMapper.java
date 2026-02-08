package com.hbnu.zen.mapper;

import com.hbnu.zen.dto.LectureSignupView;
import com.hbnu.zen.mybatis.entity.LectureSignup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LectureSignupMapper {
    int insert(LectureSignup signup);

    LectureSignup selectByLectureAndUser(@Param("lectureId") Long lectureId,
                                         @Param("userId") Long userId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") String status);

    int countByStatus(@Param("lectureId") Long lectureId,
                      @Param("status") String status);

    LectureSignup selectEarliestWaitlist(@Param("lectureId") Long lectureId,
                                         @Param("status") String status);

    List<LectureSignupView> selectMySignups(@Param("userId") Long userId);
}
