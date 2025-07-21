package com.woochang.highticket.domain.performnace.schedule;

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
public enum PerformanceScheduleStatus {
    UPCOMING("UPCOMING"),
    OPEN("OPEN"),
    CLOSE("CLOSE"),
    CANCELED("CANCELED");

    private final String value;

    private static final Map<String, PerformanceScheduleStatus> cache = Arrays.stream(values())
            .collect(Collectors.toMap(PerformanceScheduleStatus::getValue, Function.identity()));

    public static PerformanceScheduleStatus from(String value) {
        return Optional.ofNullable(cache.get(value))
                .orElseThrow(() -> new BusinessException(PERFORMANCE_SCHEDULE_NOT_EXISTS));
    }
}
