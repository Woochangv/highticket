package com.woochang.highticket.controller.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.dto.performance.PerformanceDto.Update;
import com.woochang.highticket.global.response.ApiResponse;
import com.woochang.highticket.global.response.SuccessCode;
import com.woochang.highticket.mapper.performance.PerformanceMapper;
import com.woochang.highticket.service.performance.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.woochang.highticket.dto.performance.PerformanceDto.Create;
import static com.woochang.highticket.dto.performance.PerformanceDto.Response;
import static com.woochang.highticket.global.response.SuccessCode.*;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;
    private final PerformanceMapper performanceMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<Response>> create(@RequestBody @Valid Create request) {
        Performance performance = performanceService.createPerformance(request);
        Response response = performanceMapper.toResponse(performance);
        return buildResponse(PERFORMANCE_CREATED, response);
    }

    @GetMapping("/{performanceId}")
    public ResponseEntity<ApiResponse<Response>> getPerformance(@PathVariable Long performanceId) {
        Performance performance = performanceService.getPerformance(performanceId);
        Response response = performanceMapper.toResponse(performance);
        return buildResponse(OK, response);
    }

    @PatchMapping("/{performanceId}")
    public ResponseEntity<ApiResponse<Response>> update(@PathVariable Long performanceId, @RequestBody @Valid Update request) {
        Performance performance = performanceService.updatePerformance(performanceId, request);
        Response response = performanceMapper.toResponse(performance);
        return buildResponse(PERFORMANCE_UPDATED, response);
    }

    @DeleteMapping("/{performanceId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long performanceId) {
        performanceService.deletePerformance(performanceId);
        return ResponseEntity
                .status(PERFORMANCE_DELETED.getStatus())
                .body(ApiResponse.success(PERFORMANCE_DELETED));
    }

    private ResponseEntity<ApiResponse<Response>> buildResponse(SuccessCode successCode, Response response) {
        return ResponseEntity.status(successCode.getStatus())
                .body(ApiResponse.success(successCode, response));
    }
}
