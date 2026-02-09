package com.hbnu.zen.service;

import com.hbnu.zen.dto.StudyRoomView;
import com.hbnu.zen.mapper.StudyRoomMapper;
import com.hbnu.zen.mybatis.entity.StudyRoom;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudyRoomService {
    private final StudyRoomMapper studyRoomMapper;

    public StudyRoomService(StudyRoomMapper studyRoomMapper) {
        this.studyRoomMapper = studyRoomMapper;
    }

    public List<StudyRoom> listAll() {
        return studyRoomMapper.selectAll();
    }

    public StudyRoom getById(Long id) {
        return studyRoomMapper.selectById(id);
    }

    public List<StudyRoomView> listAvailable(LocalDateTime startTime, LocalDateTime endTime) {
        return studyRoomMapper.selectAvailableViews(startTime, endTime);
    }
}
