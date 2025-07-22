package com.woochang.highticket.controller.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.global.response.ApiResponse;
import com.woochang.highticket.mapper.performance.schedule.PerformanceScheduleMapper;
import com.woochang.highticket.service.performance.schedule.PerformanceScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.*;

@RestController
@RequestMapping("/performance-schedules")
@RequiredArgsConstructor
public class PerformanceScheduleController {

    private final PerformanceScheduleService scheduleService;
    private final PerformanceScheduleMapper scheduleMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<Response>> create(@RequestBody @Valid Create request) {
        PerformanceSchedule schedule = scheduleService.createSchedule(request);
        Response response = scheduleMapper.toResponse(schedule);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Response>> getSchedule(@PathVariable Long id) {
        PerformanceSchedule schedule = scheduleService.getSchedule(id);
        Response response = scheduleMapper.toResponse(schedule);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Response>> update(@PathVariable Long id, @RequestBody @Valid Update request) {
        PerformanceSchedule schedule = scheduleService.updateSchedule(id, request);
        Response response = scheduleMapper.toResponse(schedule);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
