package com.woochang.highticket.mapper.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.dto.performance.PerformanceCreateRequest;
import com.woochang.highticket.dto.performance.PerformanceResponse;
import com.woochang.highticket.dto.performance.PerformanceUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    @Mapping(target = "startDate", source = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "endDate", source = "endDate", dateFormat = "yyyy-MM-dd")
    Performance toEntity(PerformanceCreateRequest request);

    void updateFromDto(PerformanceUpdateRequest dto, @MappingTarget Performance target);

    PerformanceResponse toResponse(Performance performance);
}
