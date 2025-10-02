package com.woochang.highticket.domain.performnace;

import com.woochang.highticket.domain.BaseTimeEntity;
import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "performances")
public class Performance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column
    private String description;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private PerformanceCategory category;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "performance")
    private List<PerformanceSchedule> schedules = new ArrayList<>();

    public Performance(String title, String description, PerformanceCategory category, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateWith(String title, String description, PerformanceCategory category, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // 연관관계 메서드
    public void addSchedule(PerformanceSchedule schedule) {
        schedules.add(schedule);
        schedule.setPerformance(this);
    }

}
