package com.woochang.highticket.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 4xx 클라이언트 오류
    COMMON_INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_INVALID_INPUT", "잘못된 입력입니다."),
    COMMON_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON_VALIDATION_FAILED", "입력값 검증에 실패했습니다."),

    AUTH_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_AUTHENTICATION_FAILED", "인증에 실패했습니다."),
    AUTH_REQUIRED_PERMISSION(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED_PERMISSION", "권한이 필요합니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    AUTH_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_REFRESH_TOKEN_EXPIRED", "Refresh Token이 만료되어 재인증이 필요합니다."),

    COMMON_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_NOT_FOUND", "리소스를 찾을 수 없습니다."),
    VENUE_NOT_FOUND(HttpStatus.NOT_FOUND, "VENUE_NOT_FOUND", "해당 ID에 해당하는 행사장을 찾을 수 없습니다."),

    // PERFORMANCE 관련
    PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "PERFORMANCE_NOT_FOUND", "해당 ID에 해당하는 공연을 찾을 수 없습니다."),
    PERFORMANCE_TITLE_BLANK(HttpStatus.BAD_REQUEST, "PERFORMANCE_TITLE_BLANK", "제목은 공백일 수 없습니다."),
    PERFORMANCE_CATEGORY_NOT_EXISTS(HttpStatus.BAD_REQUEST, "PERFORMANCE_CATEGORY_NOT_EXISTS", "존재하지 않는 카테고리입니다."),
    PERFORMANCE_DATE_INVALID(HttpStatus.BAD_REQUEST, "PERFORMANCE_DATE_INVALID", "시작일은 종료일보다 앞서야 합니다."),

    // PERFORMANCE SCHEDULE 관련
    PERFORMANCE_SCHEDULE_DATETIME_INVALID(HttpStatus.BAD_REQUEST, "PERFORMANCE_SCHEDULE_DATETIME_INVALID", "예매 시작 시각이 공연 시작 시각보다 이후일 수 없습니다."),
    PERFORMANCE_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "PERFORMANCE_SCHEDULE_NOT_FOUND", "존재하지 않는 공연 일정입니다."),
    PERFORMANCE_SCHEDULE_NOT_EXISTS(HttpStatus.BAD_REQUEST, "PERFORMANCE_SCHEDULE_NOT_EXISTS", "공연 일정 상태 값이 존재하지 않습니다"),

    // JSON 관련
    INVALID_JSON_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_JSON_REQUEST", "잘못된 요청입니다."),

    // 5xx 서버 오류
    GLOBAL_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL_INTERNAL_SERVER_ERROR", "서버 오류 입니다.");

    private final HttpStatus status; // 상태 코드
    private final String code; // 도메인 기반 응답 코드
    private final String message; // 사용자에게 전달할 메시지

    public int getStatus() {
        return status.value();
    }
}