package com.woochang.highticket.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {
    OK(HttpStatus.OK, "OK", "요청이 성공했습니다."),


    // 공연
    PERFORMANCE_CREATED(HttpStatus.CREATED, "PERFORMANCE_CREATED", "공연이 생성되었습니다."),
    PERFORMANCE_UPDATED(HttpStatus.OK, "PERFORMANCE_UPDATED", "공연이 수정되었습니다."),
    PERFORMANCE_DELETED(HttpStatus.NO_CONTENT, "PERFORMANCE_DELETED", "공연이 삭제되었습니다."),

    // 공연장
    VENUE_CREATED(HttpStatus.CREATED, "VENUE_CREATED", "공연장이 생성되었습니다."),
    VENUE_UPDATED(HttpStatus.OK, "VENUE_UPDATED", "공연장이 수정되었습니다."),
    VENUE_DELETED(HttpStatus.NO_CONTENT, "VENUE_DELETED", "공연장이 삭제되었습니다."),

    // 공연 일정
    PERFORMANCE_SCHEDULE_CREATED(HttpStatus.CREATED, "PERFORMANCE_SCHEDULE_CREATED", "공연 일정이 생성되었습니다."),
    PERFORMANCE_SCHEDULE_UPDATED(HttpStatus.OK, "PERFORMANCE_SCHEDULE_UPDATED", "공연 일정이 수정되었습니다."),
    PERFORMANCE_SCHEDULE_DELETED(HttpStatus.NO_CONTENT, "PERFORMANCE_SCHEDULE_DELETED", "공연 일정이 삭제되었습니다."),

    // 공연 일정별 좌석
    PERFORMANCE_SCHEDULE_SEAT_CREATED(HttpStatus.CREATED, "PERFORMANCE_SCHEDULE_SEAT_CREATED", "공연 일정 좌석이 생성되었습니다."),
    PERFORMANCE_SCHEDULE_SEAT_UPDATED(HttpStatus.OK, "PERFORMANCE_SCHEDULE_SEAT_UPDATED", "공연 일정 좌석이 수정되었습니다."),
    PERFORMANCE_SCHEDULE_SEAT_DELETED(HttpStatus.NO_CONTENT, "PERFORMANCE_SCHEDULE_SEAT_DELETED", "공연 일정 좌석이 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    public int getStatus() {
        return status.value();
    }
}
