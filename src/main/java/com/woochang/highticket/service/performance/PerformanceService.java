package com.woochang.highticket.service.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.domain.performnace.PerformanceCategory;
import com.woochang.highticket.dto.performance.PerformanceCreateRequest;
import com.woochang.highticket.dto.performance.PerformanceUpdateRequest;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.mapper.performance.PerformanceMapper;
import com.woochang.highticket.repository.performance.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceMapper performanceMapper;

    public Performance createPerformance(PerformanceCreateRequest dto) {
        Performance performance = performanceMapper.toEntity(dto);

        validatePerformanceDate(performance.getStartDate(), performance.getEndDate());

        return performanceRepository.save(performance);
    }

    public Performance findPerformance(Long id) {
        return performanceRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.PERFORMANCE_NOT_FOUND));
    }

    @Transactional
    public Performance updatePerformance(Long id, PerformanceUpdateRequest dto) {
        Performance performance = findPerformance(id);

        String title = performance.getTitle();
        if (dto.getTitle() != null) {
            if (dto.getTitle().isBlank()) {
                throw new BusinessException(ErrorCode.PERFORMANCE_TITLE_BLANK);
            }
            title = dto.getTitle();
        }

        PerformanceCategory category = performance.getCategory();
        if (dto.getCategory() != null) {
            category = PerformanceCategory.from(dto.getCategory());
        }

        LocalDate startDate = dto.getStartDate() != null ? dto.getStartDate() : performance.getStartDate();
        LocalDate endDate = dto.getEndDate() != null ? dto.getEndDate() : performance.getEndDate();

        validatePerformanceDate(startDate, endDate);
        String description = dto.getDescription() != null ? dto.getDescription() : performance.getDescription();

        performance.updateWith(title, description, category, startDate, endDate);
        return performance;
    }

    public void deletePerformance(Long id) {
        performanceRepository.deleteById(id);
    }

    private void validatePerformanceDate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException(ErrorCode.PERFORMANCE_DATE_INVALID);
        }
    }
}
