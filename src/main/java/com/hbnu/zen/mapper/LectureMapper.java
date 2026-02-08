package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.Lecture;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LectureMapper {
    int insert(Lecture lecture);

    int update(Lecture lecture);

    Lecture selectById(@Param("id") Long id);

    List<Lecture> selectAll(@Param("status") String status);

    int countAll();
}
