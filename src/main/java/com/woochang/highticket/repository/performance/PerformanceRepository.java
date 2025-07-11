package com.woochang.highticket.repository.performance;

import com.woochang.highticket.domain.performnace.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}
