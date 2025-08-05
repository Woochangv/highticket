package com.woochang.highticket.domain.performnace.schedule;

import com.woochang.highticket.global.common.EnumValue;
import com.woochang.highticket.global.exception.BusinessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.woochang.highticket.global.exception.ErrorCode.PERFORMANCE_SCHEDULE_NOT_EXISTS;

@Getter
@RequiredArgsConstructor
public enum PerformanceScheduleStatus implements EnumValue {
    UPCOMING("UPCOMING"),
    OPEN("OPEN"),
    CLOSE("CLOSE"),
    CANCELED("CANCELED");

    private final String value;
}
