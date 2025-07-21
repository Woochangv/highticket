package com.woochang.highticket.controller.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.global.response.ApiResponse;
import com.woochang.highticket.mapper.performance.schedule.PerformanceScheduleMapper;
import com.woochang.highticket.service.performance.schedule.PerformanceScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Create;
import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Response;

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
}
