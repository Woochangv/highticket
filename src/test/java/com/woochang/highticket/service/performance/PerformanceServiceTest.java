package com.woochang.highticket.service.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.dto.performance.PerformanceDto.Update;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.mapper.performance.PerformanceMapper;
import com.woochang.highticket.repository.performance.PerformanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.woochang.highticket.domain.performnace.PerformanceCategory.CONCERT;
import static com.woochang.highticket.domain.performnace.PerformanceCategory.MUSICAL;
import static com.woochang.highticket.dto.performance.PerformanceDto.Create;
import static com.woochang.highticket.global.exception.ErrorCode.*;
import static com.woochang.highticket.support.BusinessExceptionAssertions.assertBusinessError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    private static final String TITLE = "제목";
    private static final String DESCRIPTION = "설명";
    private static final String CATEGORY = "CONCERT";
    private static final LocalDate START_DATE = LocalDate.of(2025, 7, 1);
    private static final LocalDate END_DATE = LocalDate.of(2025, 8, 1);

    @InjectMocks
    private PerformanceService performanceService;
    @Mock
    private PerformanceRepository performanceRepository;
    @Mock
    private PerformanceMapper performanceMapper;

    private static Stream<String> invalidValues() {
        return Stream.of("INVALID", null);
    }

    @Test
    @DisplayName("공연이 정상적으로 생성된다")
    public void create_success() {
        // given
        Create request = baseCreate();

        Performance performance = new Performance(
                request.getTitle(), request.getDescription(),
                CONCERT, request.getStartDate(), request.getEndDate()
        );

        when(performanceMapper.toEntity(request)).thenReturn(performance);
        when(performanceRepository.save(performance)).thenReturn(performance);

        // when
        Performance result = performanceService.createPerformance(request);

        // then
        verify(performanceMapper).toEntity(request);
        verify(performanceRepository).save(performance);
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getDescription()).isEqualTo(request.getDescription());
        assertThat(result.getCategory()).isEqualTo(CONCERT);
        assertThat(result.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(request.getEndDate());
    }

    @Test
    @DisplayName("공연 생성 시 공연 시작일이 종료일보다 늦으면 예외가 발생한다")
    public void create_invalidDate_throwsException() {
        // given
        Create request = baseCreate();
        request.setStartDate(LocalDate.of(2025, 8, 1));
        request.setEndDate(LocalDate.of(2025, 7, 1));

        Performance performance = new Performance(
                request.getTitle(), request.getDescription(),
                CONCERT, request.getStartDate(), request.getEndDate()
        );

        when(performanceMapper.toEntity(request)).thenReturn(performance);

        // when & then
        assertBusinessError(
                () -> performanceService.createPerformance(request),
                PERFORMANCE_DATE_INVALID
        );
        verify(performanceRepository, never()).save(any());
    }

    @ParameterizedTest(name = "[{index}] category={0} -> INVALID_ENUM_VALUE")
    @DisplayName("공연 생성 시 유효하지 않은 category 값('INVALID', null)이 주어지면 예외가 발생한다")
    @MethodSource("invalidValues")
    public void create_rejects_invalidCategory(String category) {
        // given
        Create request = baseCreate();
        request.setCategory(category);

        when(performanceMapper.toEntity(argThat(req -> Objects.equals(req.getCategory(), category))))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertBusinessError(
                () -> performanceService.createPerformance(request),
                INVALID_ENUM_VALUE
        );
        verify(performanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하는 ID로 공연을 조회하면 공연이 반환된다")
    public void getPerformance_success() {
        // given 
        Long id = 1L;

        Performance performance = basePerformance();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));

        // when
        Performance result = performanceService.getPerformance(id);

        // then
        verify(performanceRepository).findById(id);
        assertThat(result.getTitle()).isEqualTo(performance.getTitle());
        assertThat(result.getDescription()).isEqualTo(performance.getDescription());
        assertThat(result.getCategory()).isEqualTo(performance.getCategory());
        assertThat(result.getStartDate()).isEqualTo(performance.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(performance.getEndDate());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 공연을 조회하면 예외가 발생한다")
    public void getPerformance_notFound_throwsException() {
        // given
        Long id = 123L;

        when(performanceRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertBusinessError(
                () -> performanceService.getPerformance(id),
                PERFORMANCE_NOT_FOUND
        );
        verify(performanceRepository).findById(id);
    }

    @Test
    @DisplayName("일부 필드(description)만 수정하면 category가 null이어도 기존 값들은 유지되고 description만 정상적으로 변경된다")
    public void update_descriptionOnly_success() {
        // given
        Long id = 1L;

        Update request = new Update();
        request.setDescription("수정된 설명");
        request.setCategory(null);

        Performance performance = basePerformance();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));

        // when
        Performance result = performanceService.updatePerformance(id, request);

        // then
        verify(performanceRepository).findById(id);
        assertThat(result.getTitle()).isEqualTo(performance.getTitle());
        assertThat(result.getDescription()).isEqualTo(request.getDescription());
        assertThat(result.getCategory()).isEqualTo(performance.getCategory());
        assertThat(result.getStartDate()).isEqualTo(performance.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(performance.getEndDate());
    }

    @Test
    @DisplayName("모든 필드가 null인 경우 예외가 발생한다")
    public void update_allFieldsNull_throwsException() {
        // given
        Long id = 1L;
        Update request = new Update();

        // when & then
        assertBusinessError(
                () -> performanceService.updatePerformance(id, request),
                PERFORMANCE_UPDATE_REQUEST_INVALID
        );
        verify(performanceRepository, never()).findById(any());
    }

    @Test
    @DisplayName("공연 수정 시 제목이 공백이면 예외가 발생한다")
    public void updatePerformance_blankTitle_throwsException() {
        // given 
        Long id = 1L;

        Update request = new Update();
        request.setTitle("");

        // when & then
        assertBusinessError(
                () -> performanceService.updatePerformance(id, request),
                PERFORMANCE_TITLE_BLANK
        );
        verify(performanceRepository, never()).findById(any());
    }

    @Test
    @DisplayName("공연 수정 시 공연 시작일이 공연 종료일보다 늦으면 예외가 발생한다")
    public void updatePerformance_invalidDate_throwsException() {
        // given
        Long id = 1L;

        Update request = new Update();
        request.setStartDate(LocalDate.of(2025, 9, 1));

        Performance performance = basePerformance();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));

        // when & then
        assertBusinessError(
                () -> performanceService.updatePerformance(id, request),
                PERFORMANCE_DATE_INVALID
        );
        verify(performanceRepository).findById(id);
    }

    @Test
    @DisplayName("공연 수정 시 유효하지 않은 category 값('INVALID')이 주어지면 예외가 발생한다")
    public void update_invalidCategoryValue_throwsException() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setCategory("INVALID");

        Performance performance = basePerformance();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));
        when(performanceMapper.toCategory("INVALID")).thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertBusinessError(
                () -> performanceService.updatePerformance(id, request),
                INVALID_ENUM_VALUE
        );
        verify(performanceRepository).findById(id);
        verify(performanceMapper).toCategory("INVALID");
    }

    @Test
    @DisplayName("공연 수정 시 category가 null이면 매퍼 변환이 호출되지 않는다")
    public void update_nullCategory_doesNotCallMapper() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setTitle("수정 제목");
        request.setCategory(null);

        Performance performance = basePerformance();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));

        // when
        performanceService.updatePerformance(id, request);

        // then
        verify(performanceRepository).findById(id);
        verify(performanceMapper, never()).toCategory(any());
    }

    @Test
    @DisplayName("공연 수정 시 category가 주어지면 매퍼 변환이 한 번 호출된다")
    public void update_category_callsMapperOnce() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setTitle("수정 제목");
        request.setCategory("MUSICAL");

        Performance performance = basePerformance();

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));
        when(performanceMapper.toCategory("MUSICAL")).thenReturn(MUSICAL);

        // when
        performanceService.updatePerformance(id, request);

        // then
        verify(performanceRepository).findById(id);
        verify(performanceMapper, times(1)).toCategory("MUSICAL");
    }

    @Test
    @DisplayName("공연 삭제 요청 시 해당 ID로 삭제가 수행된다")
    public void delete_success() {
        // given
        Long id = 1L;

        // when
        performanceService.deletePerformance(id);

        // then
        verify(performanceRepository).deleteById(id);
    }

    private Create baseCreate() {
        Create request = new Create();
        request.setTitle(TITLE);
        request.setDescription(DESCRIPTION);
        request.setCategory(CATEGORY);
        request.setStartDate(START_DATE);
        request.setEndDate(END_DATE);

        return request;
    }

    private Performance basePerformance() {
        return new Performance(TITLE, DESCRIPTION, CONCERT, START_DATE, END_DATE);
    }
}