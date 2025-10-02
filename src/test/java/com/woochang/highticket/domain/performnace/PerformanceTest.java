package com.woochang.highticket.domain.performnace;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PerformanceTest {

    @Test
    @DisplayName("공연 일정 추가 시 양방향 연관관계가 정상적으로 설정된다")
    public void addSchedule_success() {
        // given
        Performance performance = new Performance();
        PerformanceSchedule schedule = new PerformanceSchedule(
                LocalDateTime.of(2025, 7, 1, 18, 0),
                LocalDateTime.of(2025, 5, 1, 20, 0),
                10000,
                PerformanceScheduleStatus.CLOSE
        );

        // when
        performance.addSchedule(schedule);

        // then
        assertThat(performance.getSchedules()).contains(schedule);
        assertThat(schedule.getPerformance()).isEqualTo(performance);
    }
}