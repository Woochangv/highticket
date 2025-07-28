package com.woochang.highticket.domain.performnace.schedule.seat;

import com.woochang.highticket.global.common.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatGrade implements EnumValue {
    VIP("VIP"),
    R("R"),
    S("S"),
    A("A");

    private final String value;
}
