package com.woochang.highticket.service.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.dto.performance.PerformanceCreateRequest;
import com.woochang.highticket.dto.performance.PerformanceUpdateRequest;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.mapper.performance.PerformanceMapper;
import com.woochang.highticket.repository.performance.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceMapper performanceMapper;

    public Performance createPerformance(PerformanceCreateRequest dto) {
        return performanceRepository.save(performanceMapper.toEntity(dto));
    }

    public Performance findPerformance(Long id) {
        return performanceRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.PERFORMANCE_NOT_FOUND));
    }

    public Performance updatePerformance(Long id, PerformanceUpdateRequest dto) {
        Performance performance = findPerformance(id);
        performanceMapper.updateFromDto(dto, performance);
        return performance;
    }

    public void deletePerformance(Long id) {
        performanceRepository.deleteById(id);
    }
}
