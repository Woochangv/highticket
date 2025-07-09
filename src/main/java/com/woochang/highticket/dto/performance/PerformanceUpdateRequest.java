package com.woochang.highticket.dto.performance;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PerformanceUpdateRequest {

    @Size(max = 50, message = "제목은 50자 이하여야 합니다.")
    private String title;

    @Size(max = 255, message = "설명은 255자 이하여야 합니다.")
    private String description;

    private String category;

    private LocalDate startDate;

    private LocalDate endDate;

}
