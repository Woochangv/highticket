package com.woochang.highticket.global.response;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import static com.woochang.highticket.global.response.ApiResponse.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ResponseEntitySupport {

    /**
     * 데이터 포함
     */
    public static <T> ResponseEntity<ApiResponse<T>> of(SuccessCode successCode, T response) {
        return ResponseEntity
                .status(successCode.getStatus())
                .body(success(successCode, response));
    }

    /**
     * 데이터 미포함
     */
    public static ResponseEntity<ApiResponse<Void>> of(SuccessCode successCode) {
        return ResponseEntity
                .status(successCode.getStatus())
                .body(success(successCode));
    }
}
