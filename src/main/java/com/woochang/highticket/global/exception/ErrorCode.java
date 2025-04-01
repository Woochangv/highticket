package com.woochang.highticket.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 4xx 클라이언트 오류
    INVALID_INPUT(400, "INVALID_INPUT", "잘못된 입력입니다."),
    VALIDATION_FAILED(400, "VALIDATION_FAILED", "입력값 검증에 실패했습니다."),

    AUTHENTICATION_FAILED(401, "AUTHENTICATION_FAILED", "인증에 실패했습니다."),
    REQUIRED_PERMISSION(401, "REQUIRED_PERMISSION", "권한이 필요합니다."),

    NOT_FOUND(404, "NOT_FOUND", "리소스를 찾을 수 없습니다."),

    // 5xx 서버 오류
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류 입니다.");

    private final int status; // 상태 코드
    private final String code; // 도메인 기반 응답 코드
    private final String message; // 사용자에게 전달할 메시지
}
