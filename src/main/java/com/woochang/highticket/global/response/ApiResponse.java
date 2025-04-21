package com.woochang.highticket.global.response;

import lombok.Builder;
import lombok.Getter;

@Getter
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

    protected ApiResponse(){}

    // 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("200")
                .message("요청이 성공했습니다.")
                .data(data)
                .build();
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
