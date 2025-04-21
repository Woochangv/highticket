package com.woochang.highticket.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 로그 등에 출력될 메시지
        this.errorCode = errorCode;
    }
}
