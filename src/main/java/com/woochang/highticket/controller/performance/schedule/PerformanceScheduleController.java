package com.woochang.highticket.controller.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus;
import com.woochang.highticket.global.response.ApiResponse;
import com.woochang.highticket.global.response.ResponseEntitySupport;
import com.woochang.highticket.mapper.performance.schedule.PerformanceScheduleMapper;
import com.woochang.highticket.service.performance.schedule.PerformanceScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.*;
import static com.woochang.highticket.global.response.SuccessCode.*;

@RestController
@RequestMapping("/performances/{performanceId}/schedules")
@RequiredArgsConstructor
public class PerformanceScheduleController {

    private final PerformanceScheduleService scheduleService;
    private final PerformanceScheduleMapper scheduleMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<Response>> create(@PathVariable Long performanceId, @RequestBody @Valid Create request) {
        PerformanceSchedule schedule = scheduleService.createSchedule(performanceId, request);
        Response response = scheduleMapper.toResponse(schedule);
        return ResponseEntitySupport.of(PERFORMANCE_SCHEDULE_CREATED, response);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Response>> getSchedule(@PathVariable Long scheduleId) {
        PerformanceSchedule schedule = scheduleService.getSchedule(scheduleId);
        Response response = scheduleMapper.toResponse(schedule);
        return ResponseEntitySupport.of(OK, response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Response>>> getSchedules(@PathVariable Long performanceId, @RequestParam(required = false) PerformanceScheduleStatus status, Pageable pageable) {
        Page<PerformanceSchedule> schedules = scheduleService.getSchedules(performanceId, status, pageable);
        Page<Response> response = schedules.map(scheduleMapper::toResponse);
        return ResponseEntitySupport.of(OK, response);
    }



    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Response>> update(@PathVariable Long scheduleId, @RequestBody @Valid Update request) {
        PerformanceSchedule schedule = scheduleService.updateSchedule(scheduleId, request);
        Response response = scheduleMapper.toResponse(schedule);
        return ResponseEntitySupport.of(PERFORMANCE_SCHEDULE_UPDATED, response);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntitySupport.of(PERFORMANCE_SCHEDULE_DELETED);
    }
}
