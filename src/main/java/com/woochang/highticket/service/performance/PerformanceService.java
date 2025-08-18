package com.woochang.highticket.service.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.domain.performnace.PerformanceCategory;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.mapper.performance.PerformanceMapper;
import com.woochang.highticket.repository.performance.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.woochang.highticket.dto.performance.PerformanceDto.Create;
import static com.woochang.highticket.dto.performance.PerformanceDto.Update;
import static com.woochang.highticket.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceMapper performanceMapper;

    @Transactional
    public Performance createPerformance(Create request) {
        Performance performance = performanceMapper.toEntity(request);
        validatePerformanceDate(performance.getStartDate(), performance.getEndDate());
        return performanceRepository.save(performance);
    }

    public Performance getPerformance(Long id) {
        return performanceRepository.findById(id).orElseThrow(() -> new BusinessException(PERFORMANCE_NOT_FOUND));
    }

    @Transactional
    public Performance updatePerformance(Long id, Update request) {
        if (request.isAllFieldsNull()) {
            throw new BusinessException(PERFORMANCE_UPDATE_REQUEST_INVALID);
        }

        if (request.getTitle() != null && request.getTitle().isBlank()) {
                throw new BusinessException(PERFORMANCE_TITLE_BLANK);
        }

        Performance performance = getPerformance(id);

        String title = resolveValue(request.getTitle(), performance.getTitle());
        String description = resolveValue(request.getDescription(), performance.getDescription());

        PerformanceCategory category = request.getCategory() != null
                ? performanceMapper.toCategory(request.getCategory())
                : performance.getCategory();

        LocalDate startDate = resolveValue(request.getStartDate(), performance.getStartDate());
        LocalDate endDate = resolveValue(request.getEndDate(), performance.getEndDate());

        validatePerformanceDate(startDate, endDate);

        performance.updateWith(title, description, category, startDate, endDate);
        return performance;
    }

    @Transactional
    public void deletePerformance(Long id) {
        performanceRepository.deleteById(id);
    }

    private void validatePerformanceDate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException(PERFORMANCE_DATE_INVALID);
        }
    }

    private <T> T resolveValue(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }
}
