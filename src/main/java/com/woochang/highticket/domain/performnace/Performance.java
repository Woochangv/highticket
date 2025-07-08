package com.woochang.highticket.domain.performnace;

import com.woochang.highticket.domain.BaseTimeEntity;
import com.woochang.highticket.dto.performance.PerformanceUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "performances")
public class Performance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title;

    private String description;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private PerformanceCategory category;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public Performance(String title, String description, PerformanceCategory category, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateWith(PerformanceUpdateRequest request) {
        if (request.getTitle() != null) this.title = request.getTitle();
        if (request.getDescription() != null) this.description = request.getDescription();
        if (request.getCategory() != null) this.category = request.getCategory();
        if (request.getStartDate() != null) this.startDate = request.getStartDate();
        if (request.getEndDate() != null) this.endDate = request.getEndDate();
    }
}
