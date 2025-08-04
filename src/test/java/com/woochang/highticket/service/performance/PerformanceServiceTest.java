package com.woochang.highticket.service.performance;

import com.woochang.highticket.domain.performnace.Performance;
import com.woochang.highticket.dto.performance.PerformanceDto.Update;
import com.woochang.highticket.global.exception.BusinessException;
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

import static com.woochang.highticket.domain.performnace.PerformanceCategory.CONCERT;
import static com.woochang.highticket.dto.performance.PerformanceDto.Create;
import static com.woochang.highticket.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    @DisplayName("공연이 정상적으로 생성된다")
    public void create_success() {
        // given
        Create request = new Create();
        request.setTitle(TITLE);
        request.setDescription(DESCRIPTION);
        request.setCategory(CATEGORY);
        request.setStartDate(START_DATE);
        request.setEndDate(END_DATE);

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
        Create request = new Create();
        request.setTitle(TITLE);
        request.setDescription(DESCRIPTION);
        request.setCategory(CATEGORY);
        request.setStartDate(LocalDate.of(2025, 8, 1));
        request.setEndDate(LocalDate.of(2025, 7, 1));

        Performance performance = new Performance(
                request.getTitle(), request.getDescription(),
                CONCERT, request.getStartDate(), request.getEndDate()
        );

        when(performanceMapper.toEntity(request)).thenReturn(performance);

        // when & then
        assertThatThrownBy(() -> performanceService.createPerformance(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(PERFORMANCE_DATE_INVALID.getMessage());
    }

    @Test
    @DisplayName("공연 생성 시 유효하지 않은 category 값이 들어오면 예외가 발생한다")
    public void create_invalidCategoryValue_throwsException() {
        // given
        Create request = new Create();
        request.setTitle(TITLE);
        request.setDescription(DESCRIPTION);
        request.setCategory("INVALID");
        request.setStartDate(START_DATE);
        request.setEndDate(END_DATE);

        when(performanceMapper.toEntity(argThat(req -> "INVALID".equals(req.getCategory()))))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertThatThrownBy(() -> performanceService.createPerformance(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(INVALID_ENUM_VALUE.getMessage());
    }

    @Test
    @DisplayName("공연 생성 시 category 값이 null이면 예외가 발생한다")
    public void create_nullCategory_throwsException() {
        // given
        Create request = new Create();
        request.setTitle(TITLE);
        request.setDescription(DESCRIPTION);
        request.setCategory(null);
        request.setStartDate(START_DATE);
        request.setEndDate(END_DATE);

        when(performanceMapper.toEntity(argThat(req -> (req.getCategory()) == null)))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertThatThrownBy(() -> performanceService.createPerformance(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(INVALID_ENUM_VALUE.getMessage());
    }
    
    @Test
    @DisplayName("존재하는 ID로 공연을 조회하면 공연이 반환된다")
    public void getPerformance_success() {
        // given 
        Long id = 1L;

        Performance performance = new Performance(
                TITLE, DESCRIPTION,
                CONCERT, START_DATE, END_DATE);

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
        assertThatThrownBy(() -> performanceService.getPerformance(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage(PERFORMANCE_NOT_FOUND.getMessage());
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

        Performance performance = new Performance(
                TITLE, DESCRIPTION,
                CONCERT, START_DATE, END_DATE);

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
        assertThatThrownBy(() -> performanceService.updatePerformance(id, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(PERFORMANCE_UPDATE_REQUEST_INVALID.getMessage());
    }
    
    @Test
    @DisplayName("공연 수정 시 제목이 공백이면 예외가 발생한다")
    public void updatePerformance_blankTitle_throwsException() {
        // given 
        Long id = 1L;

        Update request = new Update();
        request.setTitle("");

        // when & then
        assertThatThrownBy(() -> performanceService.updatePerformance(id, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(PERFORMANCE_TITLE_BLANK.getMessage());
    }

    @Test
    @DisplayName("공연 수정 시 공연 시작일이 공연 종료일보다 늦으면 예외가 발생한다")
    public void updatePerformance_invalidDate_throwsException() {
        // given
        Long id = 1L;

        Update request = new Update();
        request.setStartDate(LocalDate.of(2025, 9, 1));

        Performance performance = new Performance(
                TITLE, DESCRIPTION,
                CONCERT, START_DATE, END_DATE);

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));

        // when & then
        assertThatThrownBy(() -> performanceService.updatePerformance(id, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(PERFORMANCE_DATE_INVALID.getMessage());
    }
    
    @Test
    @DisplayName("공연 수정 시 유효하지 않은 category 값이 들어오면 예외가 발생한다")
    public void update_invalidCategoryValue_throwsException() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setCategory("INVALID");

        Performance performance = new Performance(
                TITLE, DESCRIPTION,
                CONCERT, START_DATE, END_DATE);

        when(performanceRepository.findById(id)).thenReturn(Optional.of(performance));
        when(performanceMapper.toCategory("INVALID")).thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertThatThrownBy(() -> performanceService.updatePerformance(id, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(INVALID_ENUM_VALUE.getMessage());
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
}