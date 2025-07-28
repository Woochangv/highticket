package com.woochang.highticket.domain.performnace.schedule.seat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatStatus {
    AVAILABLE("AVAILABLE"),
    RESERVED("RESERVED"),
    SOLD("SOLD");

    private final String value;
}
