package com.woochang.highticket.service.performance.schedule;

import com.woochang.highticket.repository.performance.schedule.PerformanceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceScheduleService {

    private final PerformanceScheduleRepository scheduleRepository;

}
