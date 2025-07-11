package com.woochang.highticket.dto.performance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.woochang.highticket.domain.performnace.PerformanceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class PerformanceResponse {

    private Long id;

    private String title;

    private String description;

    private PerformanceCategory category;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
