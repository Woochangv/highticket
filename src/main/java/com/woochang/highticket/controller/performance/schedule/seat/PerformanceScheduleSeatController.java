package com.woochang.highticket.controller.performance.schedule.seat;

import com.woochang.highticket.domain.performnace.schedule.seat.PerformanceScheduleSeat;
import com.woochang.highticket.global.response.ApiResponse;
import com.woochang.highticket.global.response.SuccessCode;
import com.woochang.highticket.mapper.performance.schedule.seat.PerformanceScheduleSeatMapper;
import com.woochang.highticket.service.performance.schedule.seat.PerformanceScheduleSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.*;
import static com.woochang.highticket.global.response.SuccessCode.*;

@RestController
@RequestMapping("/performance-schedule-seats")
@RequiredArgsConstructor
public class PerformanceScheduleSeatController {

    private final PerformanceScheduleSeatService seatService;
    private final PerformanceScheduleSeatMapper seatMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<Response>> create(@RequestBody @Valid Create request) {
        PerformanceScheduleSeat seat = seatService.createSeat(request);
        Response response = seatMapper.toResponse(seat);
        return buildResponse(PERFORMANCE_SCHEDULE_SEAT_CREATED, response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Response>> getSeat(@PathVariable Long id) {
        PerformanceScheduleSeat seat = seatService.getSeat(id);
        Response response = seatMapper.toResponse(seat);
        return buildResponse(OK, response);
    }

    @PatchMapping("/{id}")
    ResponseEntity<ApiResponse<Response>> update(@PathVariable Long id, @RequestBody @Valid Update request) {
        PerformanceScheduleSeat seat = seatService.updateSeat(id, request);
        Response response = seatMapper.toResponse(seat);
        return buildResponse(PERFORMANCE_SCHEDULE_SEAT_UPDATED, response);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return ResponseEntity.status(PERFORMANCE_SCHEDULE_SEAT_DELETED.getStatus())
                .body(ApiResponse.success(PERFORMANCE_SCHEDULE_SEAT_DELETED));
    }

    private ResponseEntity<ApiResponse<Response>> buildResponse(SuccessCode successCode, Response response) {
        return ResponseEntity.status(successCode.getStatus())
                .body(ApiResponse.success(successCode, response));
    }
}
