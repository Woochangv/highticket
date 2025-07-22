package com.woochang.highticket.repository.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {
}
