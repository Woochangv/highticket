package com.woochang.highticket.controller.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.dto.performance.PerformanceCreateRequest;
import com.woochang.highticket.dto.performance.PerformanceResponse;
import com.woochang.highticket.dto.performance.PerformanceUpdateRequest;
import com.woochang.highticket.global.response.ApiResponse;
import com.woochang.highticket.mapper.performance.PerformanceMapper;
import com.woochang.highticket.service.performance.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.woochang.highticket.global.response.SuccessCode.*;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;
    private final PerformanceMapper performanceMapper;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<PerformanceResponse>>> createPerformance(@RequestBody @Valid PerformanceCreateRequest request) {
        return Mono.fromCallable(() -> {
            Performance performance = performanceService.createPerformance(request);
            PerformanceResponse response = performanceMapper.toResponse(performance);
            return ResponseEntity
                    .status(PERFORMANCE_CREATED.getStatus())
                    .body(ApiResponse.success(PERFORMANCE_CREATED, response));
        });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<PerformanceResponse>>> getPerformance(@PathVariable Long id) {
        return Mono.fromCallable(() -> {
            Performance performance = performanceService.findPerformance(id);
            PerformanceResponse response = performanceMapper.toResponse(performance);
            return ResponseEntity
                    .status(OK.getStatus())
                    .body(ApiResponse.success(OK, response));
        });
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<PerformanceResponse>>> updatePerformance(@PathVariable Long id, @RequestBody @Valid PerformanceUpdateRequest request) {
        return Mono.fromCallable(() -> {
                    Performance performance = performanceService.updatePerformance(id, request);
                    PerformanceResponse response = performanceMapper.toResponse(performance);
                    return ResponseEntity
                            .status(PERFORMANCE_UPDATED.getStatus())
                            .body(ApiResponse.success(PERFORMANCE_UPDATED, response));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deletePerformance(@PathVariable Long id) {
        return Mono.fromCallable(() -> {
            performanceService.deletePerformance(id);
            return ResponseEntity
                    .status(PERFORMANCE_DELETED.getStatus())
                    .body(ApiResponse.success(PERFORMANCE_DELETED));
        });
    }
}
