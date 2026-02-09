package com.hbnu.zen.controller;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.dto.SeatReservationView;
import com.hbnu.zen.mapper.SeatMapper;
import com.hbnu.zen.mapper.SeatReservationMapper;
import com.hbnu.zen.mapper.StudyRoomMapper;
import com.hbnu.zen.mybatis.entity.Seat;
import com.hbnu.zen.mybatis.entity.StudyRoom;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/study-rooms")
public class AdminStudyRoomController {
    private final StudyRoomMapper studyRoomMapper;
    private final SeatMapper seatMapper;
    private final SeatReservationMapper reservationMapper;

    public AdminStudyRoomController(StudyRoomMapper studyRoomMapper,
                                    SeatMapper seatMapper,
                                    SeatReservationMapper reservationMapper) {
        this.studyRoomMapper = studyRoomMapper;
        this.seatMapper = seatMapper;
        this.reservationMapper = reservationMapper;
    }

    @GetMapping
    public ApiResponse<List<StudyRoom>> listAll() {
        return ApiResponse.success(studyRoomMapper.selectAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<StudyRoom> getById(@PathVariable Long id) {
        return ApiResponse.success(studyRoomMapper.selectById(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody StudyRoom room) {
        studyRoomMapper.insert(room);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody StudyRoom room) {
        room.setId(id);
        studyRoomMapper.update(room);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        studyRoomMapper.updateStatus(id, 0);
        return ApiResponse.success();
    }

    @GetMapping("/{roomId}/seats")
    public ApiResponse<List<Seat>> listSeats(@PathVariable Long roomId) {
        return ApiResponse.success(seatMapper.selectByRoomId(roomId));
    }

    @PostMapping("/{roomId}/seats")
    public ApiResponse<Void> createSeat(@PathVariable Long roomId, @RequestBody Seat seat) {
        seat.setStudyRoomId(roomId);
        seatMapper.insert(seat);
        return ApiResponse.success();
    }

    @PostMapping("/{roomId}/seats/batch")
    public ApiResponse<Void> batchCreateSeats(@PathVariable Long roomId, @RequestBody List<Seat> seats) {
        for (Seat seat : seats) {
            seat.setStudyRoomId(roomId);
            seatMapper.insert(seat);
        }
        return ApiResponse.success();
    }

    @PutMapping("/seats/{id}")
    public ApiResponse<Void> updateSeat(@PathVariable Long id, @RequestBody Seat seat) {
        seat.setId(id);
        seatMapper.update(seat);
        return ApiResponse.success();
    }

    @DeleteMapping("/seats/{id}")
    public ApiResponse<Void> deleteSeat(@PathVariable Long id) {
        seatMapper.updateStatus(id, 0);
        return ApiResponse.success();
    }

    @GetMapping("/reservations")
    public ApiResponse<List<SeatReservationView>> listReservations() {
        return ApiResponse.success(reservationMapper.selectAllViews());
    }
}
