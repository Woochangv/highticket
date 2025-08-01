package com.woochang.highticket.dto.performance.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceScheduleDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Create {
        @NotNull(message = "공연 시작 시간은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime startDatetime;

        @NotNull(message = "티켓 예매 시작 시간은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime ticketOpenAt;

        @NotNull(message = "티켓 수 제한은 필수입니다.")
        @Positive
        private Integer ticketLimit;

        @NotNull(message = "공연 일정 상태는 필수입니다.")
        @Size(max = 10, message = "공연 일정 상태는 최대 10자 이하로 입력해야 합니다.")
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Update {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime startDatetime;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime ticketOpenAt;

        private Integer ticketLimit;

        @Size(max = 10, message = "공연 일정 상태는 최대 10자 이하로 입력해야 합니다.")
        private String status;

        public boolean isAllFieldNull() {
            return startDatetime == null && ticketOpenAt == null && ticketLimit == null && status == null;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private Long id;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime startDatetime;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime ticketOpenAt;

        private int ticketLimit;

        private PerformanceScheduleStatus status;
    }
}
