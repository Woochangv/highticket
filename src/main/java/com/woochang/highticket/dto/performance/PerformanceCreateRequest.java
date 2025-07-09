package com.woochang.highticket.dto.performance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PerformanceCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 50, message = "제목은 50자 이하여야 합니다.")
    private String title;

    @Size(max = 255, message = "설명은 255자 이하여야 합니다.")
    private String description;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    @NotNull(message = "공연 시작일은 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "공연 종료일은 필수입니다.")
    private LocalDate endDate;

}
