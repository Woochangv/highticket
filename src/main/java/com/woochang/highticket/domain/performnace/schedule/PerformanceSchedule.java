package com.woochang.highticket.domain.performnace.schedule;

import com.woochang.highticket.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "Performance_schedules")
public class PerformanceSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDatetime;

    private LocalDateTime ticketOpenAt;

    private int ticketLimit;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PerformanceScheduleStatus status;

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

}
