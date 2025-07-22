package com.woochang.highticket.mapper.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus;
import org.mapstruct.*;

import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.*;

@Mapper(componentModel = "spring")
public interface PerformanceScheduleMapper {

    @Mapping(target = "status", qualifiedByName = "toScheduleStatus")
    PerformanceSchedule toEntity(Create request);

    Response toResponse(PerformanceSchedule schedule);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(@MappingTarget PerformanceSchedule performanceSchedule, Update request);

    @Named("toScheduleStatus")
    default PerformanceScheduleStatus toScheduleStatus(String status) {
        return PerformanceScheduleStatus.from(status);
    }
}
