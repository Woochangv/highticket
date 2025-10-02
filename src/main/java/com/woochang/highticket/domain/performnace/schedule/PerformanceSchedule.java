package com.woochang.highticket.domain.performnace.schedule;

import com.woochang.highticket.domain.BaseTimeEntity;
import com.woochang.highticket.domain.performnace.Performance;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "Performance_schedules")
public class PerformanceSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "ticket_open_at", nullable = false)
    private LocalDateTime ticketOpenAt;

    @Column(name = "ticket_limit", nullable = false)
    private int ticketLimit;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private PerformanceScheduleStatus status;

    @ManyToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    public PerformanceSchedule(LocalDateTime startDatetime, LocalDateTime ticketOpenAt, int ticketLimit, PerformanceScheduleStatus status) {
        this.startDatetime = startDatetime;
        this.ticketOpenAt = ticketOpenAt;
        this.ticketLimit = ticketLimit;
        this.status = status;
    }

    public void updateWith(LocalDateTime startDatetime, LocalDateTime ticketOpenAt, int ticketLimit, PerformanceScheduleStatus status) {
        this.startDatetime = startDatetime;
        this.ticketOpenAt = ticketOpenAt;
        this.ticketLimit = ticketLimit;
        this.status = status;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }
}
