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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Response>> getPerformance(@PathVariable Long id) {
        Performance performance = performanceService.getPerformance(id);
        Response response = performanceMapper.toResponse(performance);
        return buildResponse(OK, response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Response>> update(@PathVariable Long id, @RequestBody @Valid Update request) {
        Performance performance = performanceService.updatePerformance(id, request);
        Response response = performanceMapper.toResponse(performance);
        return buildResponse(PERFORMANCE_UPDATED, response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        performanceService.deletePerformance(id);
        return ResponseEntity
                .status(PERFORMANCE_DELETED.getStatus())
                .body(ApiResponse.success(PERFORMANCE_DELETED));
    }

    private ResponseEntity<ApiResponse<Response>> buildResponse(SuccessCode successCode, Response response) {
        return ResponseEntity.status(successCode.getStatus())
                .body(ApiResponse.success(successCode, response));
    }
}
