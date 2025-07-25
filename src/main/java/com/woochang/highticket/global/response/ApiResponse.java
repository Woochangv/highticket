package com.woochang.highticket.global.response;

import com.woochang.highticket.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private Object data;

    @Builder
    public ApiResponse(boolean success, String code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ApiResponse<Void> success(SuccessCode successCode) {
        return ApiResponse.<Void>builder()
                .success(true)
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .build();
    }

    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(message)
                .build();
    }
}
