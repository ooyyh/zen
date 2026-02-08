package com.hbnu.zen.mapper;

import com.hbnu.zen.mybatis.entity.Classroom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassroomMapper {
    int insert(Classroom classroom);

    int update(Classroom classroom);

    Classroom selectById(@Param("id") Long id);

    List<Classroom> selectAll(@Param("building") String building,
                              @Param("minCapacity") Integer minCapacity);

    int countAll();
}
