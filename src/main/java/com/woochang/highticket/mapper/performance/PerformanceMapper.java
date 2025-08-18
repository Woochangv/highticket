package com.woochang.highticket.mapper.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.domain.performnace.PerformanceCategory;
import com.woochang.highticket.global.util.EnumUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.woochang.highticket.dto.performance.PerformanceDto.Create;
import static com.woochang.highticket.dto.performance.PerformanceDto.Response;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    @Mapping(target = "category", qualifiedByName = "toCategory")
    @Mapping(target = "startDate", source = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "endDate", source = "endDate", dateFormat = "yyyy-MM-dd")
    Performance toEntity(Create request);

    Response toResponse(Performance performance);

    @Named("toCategory")
    default PerformanceCategory toCategory(String category) {
        return EnumUtils.fromValue(PerformanceCategory.class, category);
    }
}
