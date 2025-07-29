package com.woochang.highticket.domain.performnace.schedule.seat;

import com.woochang.highticket.global.common.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatStatus implements EnumValue {
    AVAILABLE("AVAILABLE"),
    RESERVED("RESERVED"),
    SOLD("SOLD");

    private final String value;
}
