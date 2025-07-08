package com.woochang.highticket.dto.performance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String description;

    @NotBlank (message = "카테고리는 필수입니다.")
    private String category;

    @NotNull(message = "공연 시작일은 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "공연 종료일은 필수입니다.")
    private LocalDate endDate;

}
