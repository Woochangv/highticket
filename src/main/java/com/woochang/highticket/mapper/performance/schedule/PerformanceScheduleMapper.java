package com.woochang.highticket.mapper.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PerformanceScheduleMapper {

    PerformanceSchedule toEntity(PerformanceScheduleDto.Create request);

    PerformanceScheduleDto.Response toResponse(PerformanceSchedule schedule);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(@MappingTarget PerformanceSchedule performanceSchedule, PerformanceScheduleDto.Update request);
}
