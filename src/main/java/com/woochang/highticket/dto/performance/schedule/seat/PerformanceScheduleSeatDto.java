package com.woochang.highticket.dto.performance.schedule.seat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceScheduleSeatDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Create {
        @NotNull(message = "좌석 문자열은 필수입니다.")
        private String seatCode;

        @NotNull(message = "좌석 등급은 필수입니다.")
        @Size(max = 5, message = "좌석 등급은 최대 5자 이하로 입력해야 합니다.")
        private String grade;

        @NotNull(message = "좌석 가격은 필수입니다.")
        private int price;

        @NotNull(message = "좌석 상태는 필수입니다.")
        @Size(max = 10, message = "좌석 상태는 최대 10자 이하로 입력해야 합니다.")
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Update {
        private String seatCode;

        @Size(max = 5, message = "좌석 등급은 최대 5자 이하로 입력해야 합니다.")
        private String grade;

        private Integer price;

        @Size(max = 10, message = "좌석 상태는 최대 10자 이하로 입력해야 합니다.")
        private String status;

        public boolean isAllFieldsNull() {
            return seatCode == null && grade == null && price == null && status == null;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private Long id;

        private String seatCode;

        private String grade;

        private Integer price;

        private String status;
    }
}
