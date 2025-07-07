package com.woochang.highticket.dto.performance;

import com.woochang.highticket.domain.performnace.PerformanceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceCreateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private PerformanceCategory category;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    public PerformanceCreateRequest(String title, String description, PerformanceCategory category, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
