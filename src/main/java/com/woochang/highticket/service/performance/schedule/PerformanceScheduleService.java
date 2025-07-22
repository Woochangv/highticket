package com.woochang.highticket.service.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.mapper.performance.schedule.PerformanceScheduleMapper;
import com.woochang.highticket.repository.performance.schedule.PerformanceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Create;
import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Update;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceScheduleService {

    private final PerformanceScheduleRepository scheduleRepository;
    private final PerformanceScheduleMapper scheduleMapper;

    @Transactional
    public PerformanceSchedule createSchedule(Create request) {
        PerformanceSchedule schedule = scheduleMapper.toEntity(request);

        if (!schedule.getStartDatetime().isAfter(schedule.getTicketOpenAt())) {
            throw new BusinessException(ErrorCode.PERFORMANCE_SCHEDULE_DATETIME_INVALID);
        }

        return scheduleRepository.save(schedule);
    }

    public PerformanceSchedule getSchedule(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() ->
                new BusinessException(ErrorCode.PERFORMANCE_SCHEDULE_NOT_FOUND));
    }

    @Transactional
    public PerformanceSchedule updateSchedule(Long id, Update request) {
        if (request.isAllFieldNull()) {
            throw new BusinessException(ErrorCode.PERFORMANCE_SCHEDULE_UPDATE_REQUEST_INVALID);
        }

        PerformanceSchedule schedule = getSchedule(id);

        LocalDateTime startDatetime = schedule.getStartDatetime();
        LocalDateTime ticketOpenAt = schedule.getTicketOpenAt();
        int ticketLimit = schedule.getTicketLimit();
        PerformanceScheduleStatus status = schedule.getStatus();

        if (request.getStartDatetime() != null) startDatetime = request.getStartDatetime();
        if (request.getTicketOpenAt() != null) ticketOpenAt = request.getTicketOpenAt();
        if (request.getTicketLimit() != null) ticketLimit = request.getTicketLimit();
        if (request.getStatus() != null) status = scheduleMapper.toScheduleStatus(request.getStatus());

        schedule.updateWith(startDatetime, ticketOpenAt, ticketLimit, status);
        return schedule;
    }

    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
