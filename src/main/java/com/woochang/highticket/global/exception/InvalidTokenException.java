package com.woochang.highticket.global.exception;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException{

    private final ErrorCode errorCode;

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
