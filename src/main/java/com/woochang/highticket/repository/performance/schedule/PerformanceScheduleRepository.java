package com.woochang.highticket.repository.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {
    Page<PerformanceSchedule> findByPerformanceId(Long performanceId, Pageable pageable);

    Page<PerformanceSchedule> findByPerformanceIdAndStatus(Long performanceId, PerformanceScheduleStatus status, Pageable pageable);

}
