package com.woochang.highticket.domain.performnace;

import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;

import java.util.Arrays;

public enum PerformanceCategory {
    CONCERT,                // 콘서트
    MUSICAL,                // 뮤지컬
    PLAY,                   // 연극
    FANMEETING,             // 팬미팅
    ETC;                    // 기타

    public static PerformanceCategory from(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PERFORMANCE_CATEGORY_BAD_REQUEST));
    }
}
