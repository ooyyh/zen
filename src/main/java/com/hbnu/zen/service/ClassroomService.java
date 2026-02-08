package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.mapper.ClassroomMapper;
import com.hbnu.zen.mybatis.entity.Classroom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomService {
    private final ClassroomMapper classroomMapper;

    public ClassroomService(ClassroomMapper classroomMapper) {
        this.classroomMapper = classroomMapper;
    }

    public Classroom create(Classroom classroom) {
        if (classroom.getStatus() == null) {
            classroom.setStatus(1);
        }
        classroomMapper.insert(classroom);
        return classroom;
    }

    public Classroom update(Classroom classroom) {
        Classroom existing = classroomMapper.selectById(classroom.getId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教室不存在");
        }
        if (classroom.getStatus() == null) {
            classroom.setStatus(existing.getStatus());
        }
        classroomMapper.update(classroom);
        return classroomMapper.selectById(classroom.getId());
    }

    public Classroom getById(Long id) {
        Classroom classroom = classroomMapper.selectById(id);
        if (classroom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教室不存在");
        }
        return classroom;
    }

    public List<Classroom> list(String building, Integer minCapacity) {
        return classroomMapper.selectAll(building, minCapacity);
    }
}
