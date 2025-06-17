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

    // 5xx 서버 오류
    GLOBAL_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL_INTERNAL_SERVER_ERROR", "서버 오류 입니다.");

    private final HttpStatus status; // 상태 코드
    private final String code; // 도메인 기반 응답 코드
    private final String message; // 사용자에게 전달할 메시지

    public int getStatus() {
        return status.value();
    }
}