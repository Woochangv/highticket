package com.woochang.highticket.dto.performance;

import com.woochang.highticket.domain.performnace.PerformanceCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceUpdateRequest {

    private String title;

    private String description;

    private PerformanceCategory category;

    private LocalDate startDate;

    private LocalDate endDate;

}
