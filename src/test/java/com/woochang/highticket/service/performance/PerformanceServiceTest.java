package com.woochang.highticket.service.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.domain.performnace.PerformanceCategory;
import com.woochang.highticket.dto.performance.PerformanceCreateRequest;
import com.woochang.highticket.dto.performance.PerformanceUpdateRequest;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.mapper.performance.PerformanceMapper;
import com.woochang.highticket.repository.performance.PerformanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @InjectMocks
    private PerformanceService performanceService;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private PerformanceMapper performanceMapper;

    @Test
    @DisplayName("공연이 정상적으로 생성된다")
    public void createPerformance_success() {
        // given
        PerformanceCreateRequest dto = PerformanceCreateRequest.builder()
                .title("제목")
                .description("설명")
                .category("CONCERT")
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2025, 8, 1))
                .build();

        Performance performance = Performance.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(PerformanceCategory.CONCERT)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        when(performanceMapper.toEntity(dto)).thenReturn(performance);
        when(performanceRepository.save(performance)).thenReturn(performance);

        // when
        Performance result = performanceService.createPerformance(dto);

        // then
        assertThat(result.getTitle()).isEqualTo(dto.getTitle());
        assertThat(result.getDescription()).isEqualTo(dto.getDescription());
        assertThat(result.getCategory()).isEqualTo(PerformanceCategory.CONCERT);
        assertThat(result.getStartDate()).isEqualTo(dto.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(dto.getEndDate());
    }

    @Test
    @DisplayName("공연 생성 시 공연 시작일이 종료일보다 늦으면 예외가 발생한다")
    public void createPerformance_invalidDate_throwsException() {
        // given
        PerformanceCreateRequest dto = PerformanceCreateRequest.builder()
                .title("제목")
                .description("설명")
                .category("CONCERT")
                .startDate(LocalDate.of(2025, 8, 1))
                .endDate(LocalDate.of(2025, 7, 1))
                .build();

        Performance performance = Performance.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(PerformanceCategory.CONCERT)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        when(performanceMapper.toEntity(dto)).thenReturn(performance);

        // when & then
        assertThatThrownBy(() -> performanceService.createPerformance(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PERFORMANCE_DATE_INVALID.getMessage());
    }
    
    @Test
    @DisplayName("존재하는 ID로 공연을 조회하면 공연이 반환된다")
    public void findPerformance_success() {
        // given 
        Long id = 1L;
        Performance performance = Performance.builder().title("제목").build();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));

        // when
        Performance result = performanceService.findPerformance(id);

        // then
        assertThat(result.getTitle()).isEqualTo(performance.getTitle());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 공연을 조회하면 예외가 발생한다")
    public void findPerformance_notFound_throwsException() {
        // given
        Long id = 123L;

        when(performanceRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> performanceService.findPerformance(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PERFORMANCE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("존재하는 ID로 공연을 수정하면 수정된 공연이 반환된다")
    public void updatePerformance_success() {
        // given
        Long id = 1L;
        PerformanceUpdateRequest dto = PerformanceUpdateRequest.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .category("MUSICAL")
                .startDate(LocalDate.of(2025, 8, 1))
                .endDate(LocalDate.of(2025, 9, 1))
                .build();

        Performance existing = Performance.builder()
                .title("기존 제목")
                .description("기존 설명")
                .category(PerformanceCategory.CONCERT)
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2025, 8, 1))
                .build();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        Performance result = performanceService.updatePerformance(id, dto);

        // then
        assertThat(result.getTitle()).isEqualTo(dto.getTitle());
        assertThat(result.getDescription()).isEqualTo(dto.getDescription());
        assertThat(result.getCategory()).isEqualTo(PerformanceCategory.MUSICAL);
        assertThat(result.getStartDate()).isEqualTo(dto.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(dto.getEndDate());
    }
    
    @Test
    @DisplayName("공연 수정 시 제목이 공백이면 예외가 발생한다")
    public void updatePerformance_blankTitle_throwsException() {
        // given 
        Long id = 1L;

        PerformanceUpdateRequest dto = PerformanceUpdateRequest.builder()
                .title("")
                .build();

        Performance existing = Performance.builder()
                .title("기존 제목")
                .description("기존 설명")
                .category(PerformanceCategory.CONCERT)
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2025, 8, 1))
                .build();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(existing));

        // when & then
        assertThatThrownBy(() -> performanceService.updatePerformance(id, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PERFORMANCE_TITLE_BLANK.getMessage());
    }
    
    @Test
    @DisplayName("공연 수정 시 존재하지 않는 카테고리면 예외가 발생한다")
    public void updatePerformance_invalidCategory_throwsException() {
        // given 
        Long id = 1L;

        PerformanceUpdateRequest dto = PerformanceUpdateRequest.builder()
                .category("INVALID")
                .build();

        Performance existing = Performance.builder()
                .title("기존 제목")
                .description("기존 설명")
                .category(PerformanceCategory.CONCERT)
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2025, 8, 1))
                .build();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(existing));

        // when & then
        assertThatThrownBy(() -> performanceService.updatePerformance(id, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PERFORMANCE_CATEGORY_NOT_EXISTS.getMessage());
    }

    @Test
    @DisplayName("공연 수정 시 공연 시작일이 공연 종료일보다 늦으면 예외가 발생한다")
    public void updatePerformance_invalidDate_throwsException() {
        // given
        Long id = 1L;

        PerformanceUpdateRequest dto = PerformanceUpdateRequest.builder()
                .startDate(LocalDate.of(2025, 9, 1))
                .build();

        Performance existing = Performance.builder()
                .title("기존 제목")
                .description("기존 설명")
                .category(PerformanceCategory.CONCERT)
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2025, 8, 1))
                .build();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(existing));

        // when & then
        assertThatThrownBy(() -> performanceService.updatePerformance(id, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PERFORMANCE_DATE_INVALID.getMessage());
    }
}