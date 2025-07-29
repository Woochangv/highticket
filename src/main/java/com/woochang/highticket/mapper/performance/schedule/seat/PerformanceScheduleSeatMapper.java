package com.woochang.highticket.mapper.performance.schedule.seat;

import com.woochang.highticket.domain.performnace.schedule.seat.PerformanceScheduleSeat;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatGrade;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Create;
import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Response;
import static com.woochang.highticket.global.util.EnumUtils.fromValue;

@Mapper(componentModel = "spring")
public interface PerformanceScheduleSeatMapper {
    @Mapping(target = "grade", qualifiedByName = "toSeatGrade")
    @Mapping(target = "status", qualifiedByName = "toSeatStatus")
    PerformanceScheduleSeat toEntity(Create request);

    Response toResponse(PerformanceScheduleSeat seat);

    @Named("toSeatGrade")
    default SeatGrade toSeatGrade(String grade) {
        return fromValue(SeatGrade.class, grade);
    }

    @Named("toSeatStatus")
    default SeatStatus toSeatStatus(String status) {
        return fromValue(SeatStatus.class, status);
    }
}
