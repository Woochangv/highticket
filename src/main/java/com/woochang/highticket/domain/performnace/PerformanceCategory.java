package com.woochang.highticket.domain.performnace;

import com.woochang.highticket.global.common.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformanceCategory implements EnumValue {
    CONCERT("CONCERT"),                // 콘서트
    MUSICAL("MUSICAL"),                // 뮤지컬
    PLAY("PLAY"),                   // 연극
    FANMEETING("FANMEETING"),             // 팬미팅
    ETC("ETC");                    // 기타

    private final String value;
}
