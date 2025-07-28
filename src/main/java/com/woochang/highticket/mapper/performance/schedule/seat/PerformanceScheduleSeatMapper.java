package com.woochang.highticket.mapper.performance.schedule.seat;

import com.woochang.highticket.domain.performnace.schedule.seat.PerformanceScheduleSeat;
import org.mapstruct.Mapper;

import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Create;
import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Response;

@Mapper(componentModel = "spring")
public interface PerformanceScheduleSeatMapper {

    PerformanceScheduleSeat toEntity(Create request);

    Response toResponse(PerformanceScheduleSeat seat);
}
