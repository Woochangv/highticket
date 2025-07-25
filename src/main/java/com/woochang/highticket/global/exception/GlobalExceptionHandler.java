package com.woochang.highticket.global.exception;

import com.woochang.highticket.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 서비스 로직 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode()));
    }

    // 예기치 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("예상하지 못한 오류 발생: ", e);
        return ResponseEntity
                .status(ErrorCode.GLOBAL_INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.error(ErrorCode.GLOBAL_INTERNAL_SERVER_ERROR));
    }

    //
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ApiResponse<?>> handleServerWebInputException() {
        return ResponseEntity
                .status(ErrorCode.INVALID_JSON_REQUEST.getStatus())
                .body(ApiResponse.error(ErrorCode.INVALID_JSON_REQUEST));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiResponse<?>> handleWebExchangeBindException(WebExchangeBindException e) {
        String message = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(ErrorCode.COMMON_VALIDATION_FAILED.getMessage());


        return ResponseEntity
                .status(ErrorCode.COMMON_VALIDATION_FAILED.getStatus())
                .body(ApiResponse.error(ErrorCode.COMMON_VALIDATION_FAILED, message));
    }
}
