package com.woochang.highticket.mapper.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.domain.performnace.PerformanceCategory;
import com.woochang.highticket.dto.performance.PerformanceCreateRequest;
import com.woochang.highticket.dto.performance.PerformanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    @Mapping(target = "category", expression = "java(toCategory(request.getCategory()))")
    @Mapping(target = "startDate", source = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "endDate", source = "endDate", dateFormat = "yyyy-MM-dd")
    Performance toEntity(PerformanceCreateRequest request);

    PerformanceResponse toResponse(Performance performance);

    default PerformanceCategory toCategory(String category) {
        return PerformanceCategory.from(category);
    }
}
