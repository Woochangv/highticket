package com.woochang.highticket.service.performance.schedule;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.mapper.performance.schedule.PerformanceScheduleMapper;
import com.woochang.highticket.repository.performance.PerformanceRepository;
import com.woochang.highticket.repository.performance.schedule.PerformanceScheduleRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Create;
import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Update;
import static com.woochang.highticket.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceScheduleService {

    private final PerformanceScheduleRepository scheduleRepository;
    private final PerformanceScheduleMapper scheduleMapper;
    private final PerformanceRepository performanceRepository;

    @Transactional
    public PerformanceSchedule createSchedule(Long performanceId, Create request) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new BusinessException(PERFORMANCE_NOT_FOUND));

        PerformanceSchedule schedule = scheduleMapper.toEntity(request);

        if (!schedule.getStartDatetime().isAfter(schedule.getTicketOpenAt())) {
            throw new BusinessException(PERFORMANCE_SCHEDULE_DATETIME_INVALID);
        }

        performance.addSchedule(schedule);

        return scheduleRepository.save(schedule);
    }

    public PerformanceSchedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new BusinessException(PERFORMANCE_SCHEDULE_NOT_FOUND));
    }

    public Page<PerformanceSchedule> getSchedules(Long performanceId, @Nullable PerformanceScheduleStatus status, Pageable pageable) {

        if (status != null) {
            return scheduleRepository.findByPerformanceIdAndStatus(performanceId, status, pageable);
        }
        return scheduleRepository.findByPerformanceId(performanceId, pageable);
    }

    @Transactional
    public PerformanceSchedule updateSchedule(Long scheduleId, Update request) {
        if (request.isAllFieldsNull()) {
            throw new BusinessException(PERFORMANCE_SCHEDULE_UPDATE_REQUEST_INVALID);
        }

        PerformanceSchedule schedule = getSchedule(scheduleId);

        LocalDateTime startDatetime = resolveValue(request.getStartDatetime(), schedule.getStartDatetime());
        LocalDateTime ticketOpenAt = resolveValue(request.getTicketOpenAt(), schedule.getTicketOpenAt());
        int ticketLimit = resolveValue(request.getTicketLimit(), schedule.getTicketLimit());
        PerformanceScheduleStatus status = request.getStatus() != null
                ? scheduleMapper.toScheduleStatus(request.getStatus())
                : schedule.getStatus();

        schedule.updateWith(startDatetime, ticketOpenAt, ticketLimit, status);
        return schedule;
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }

    private <T> T resolveValue(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }
}
