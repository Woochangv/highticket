package com.woochang.highticket.mapper.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus;
import com.woochang.highticket.global.util.EnumUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Create;
import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Response;

@Mapper(componentModel = "spring")
public interface PerformanceScheduleMapper {

    @Mapping(target = "status", qualifiedByName = "toScheduleStatus")
    PerformanceSchedule toEntity(Create request);

    Response toResponse(PerformanceSchedule schedule);

    @Named("toScheduleStatus")
    default PerformanceScheduleStatus toScheduleStatus(String status) {
        return EnumUtils.fromValue(PerformanceScheduleStatus.class, status);
    }
}
