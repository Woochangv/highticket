package com.woochang.highticket.service.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.mapper.performance.schedule.PerformanceScheduleMapper;
import com.woochang.highticket.repository.performance.schedule.PerformanceScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus.UPCOMING;
import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Create;
import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Update;
import static com.woochang.highticket.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PerformanceScheduleServiceTest {

    private static final LocalDateTime START_DATETIME = LocalDateTime.of(2025, 7, 1, 18, 30);
    private static final LocalDateTime TICKET_OPEN_AT = LocalDateTime.of(2025, 6, 1, 20, 30);
    private static final LocalDateTime INVALID_START_DATETIME = LocalDateTime.of(2025, 5, 1, 18, 30);
    private static final int TICKET_LIMIT = 10000;
    private static final String STATUS = "UPCOMING";

    @InjectMocks
    private PerformanceScheduleService scheduleService;
    @Mock
    private PerformanceScheduleRepository scheduleRepository;
    @Mock
    private PerformanceScheduleMapper scheduleMapper;


    @Test
    @DisplayName("공연 일정이 정상적으로 생성된다")
    public void create_success() {
        // given
        Create request = new Create();
        request.setStartDatetime(START_DATETIME);
        request.setTicketOpenAt(TICKET_OPEN_AT);
        request.setTicketLimit(TICKET_LIMIT);
        request.setStatus(STATUS);

        PerformanceSchedule schedule = new PerformanceSchedule(
                request.getStartDatetime(), request.getTicketOpenAt(),
                request.getTicketLimit(), UPCOMING
        );

        when(scheduleMapper.toEntity(request)).thenReturn(schedule);
        when(scheduleRepository.save(schedule)).thenReturn(schedule);

        // when
        PerformanceSchedule result = scheduleService.createSchedule(request);

        // then
        verify(scheduleMapper).toEntity(request);
        verify(scheduleRepository).save(schedule);
        assertThat(result.getStartDatetime()).isEqualTo(request.getStartDatetime());
        assertThat(result.getTicketOpenAt()).isEqualTo(request.getTicketOpenAt());
        assertThat(result.getTicketLimit()).isEqualTo(request.getTicketLimit());
        assertThat(result.getStatus()).isEqualTo(UPCOMING);
    }

    @Test
    @DisplayName("예매 시작 시각이 공연 시작 시각보다 이후이면 예외가 발생한다")
    public void create_invalidDatetime_throwsException() {
        // given
        Create request = new Create();
        request.setStartDatetime(INVALID_START_DATETIME);
        request.setTicketOpenAt(TICKET_OPEN_AT);
        request.setTicketLimit(TICKET_LIMIT);
        request.setStatus(STATUS);

        PerformanceSchedule schedule = new PerformanceSchedule(
                request.getStartDatetime(), request.getTicketOpenAt(),
                request.getTicketLimit(), UPCOMING
        );

        when(scheduleMapper.toEntity(request)).thenReturn(schedule);

        // when & then
        assertThatThrownBy(() -> scheduleService.createSchedule(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException ex = (BusinessException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(PERFORMANCE_SCHEDULE_DATETIME_INVALID);
                });
    }

    @Test
    @DisplayName("공연 일정 생성 시 유효하지 않은 status 값이 들어오면 예외가 발생한다")
    public void create_invalidStatusValue_throwsException() {
        // given
        Create request = new Create();
        request.setStartDatetime(INVALID_START_DATETIME);
        request.setTicketOpenAt(TICKET_OPEN_AT);
        request.setTicketLimit(TICKET_LIMIT);
        request.setStatus("INVALID");

        when(scheduleMapper.toEntity(argThat(req -> request.getStatus().equals(req.getStatus()))))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertThatThrownBy(() -> scheduleService.createSchedule(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException ex = (BusinessException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(INVALID_ENUM_VALUE);
                });
    }

    @Test
    @DisplayName("공연 일정 생성 시 status 값이 null이면 예외가 발생한다")
    public void create_nullStatus_throwsException() {
        // given
        Create request = new Create();
        request.setStartDatetime(INVALID_START_DATETIME);
        request.setTicketOpenAt(TICKET_OPEN_AT);
        request.setTicketLimit(TICKET_LIMIT);
        request.setStatus(null);

        when(scheduleMapper.toEntity(argThat(req -> req.getStatus() == null)))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertThatThrownBy(() -> scheduleService.createSchedule(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException ex = (BusinessException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(INVALID_ENUM_VALUE);
                });
    }

    @Test
    @DisplayName("존재하는 ID로 공연 일정 조회하면 공연 일정을 반환한다")
    public void getSchedule_success() {
        // given
        Long id = 1L;
        PerformanceSchedule schedule = new PerformanceSchedule(
                START_DATETIME, TICKET_OPEN_AT,
                TICKET_LIMIT, UPCOMING
        );

        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));

        // when
        PerformanceSchedule result = scheduleService.getSchedule(id);

        // then
        verify(scheduleRepository).findById(id);
        assertThat(result).isNotNull();
        assertThat(result.getStartDatetime()).isEqualTo(START_DATETIME);
        assertThat(result.getTicketOpenAt()).isEqualTo(TICKET_OPEN_AT);
        assertThat(result.getTicketLimit()).isEqualTo(TICKET_LIMIT);
        assertThat(result.getStatus()).isEqualTo(UPCOMING);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
    public void getSchedule_notFound_throwsException() {
        // given
        Long id = 1L;

        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> scheduleService.getSchedule(id))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException ex = (BusinessException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(PERFORMANCE_SCHEDULE_NOT_FOUND);
                });
        verify(scheduleRepository).findById(id);
    }

    @Test
    @DisplayName("일부 필드(ticketOpenAt)만 수정하면 나머지는 유지되고 정상적으로 변경된다")
    public void update_ticketOpenAtOnly_success() {
        // given
        Long id = 1L;
        PerformanceSchedule schedule = new PerformanceSchedule(START_DATETIME, TICKET_OPEN_AT, TICKET_LIMIT, UPCOMING);
        Update request = new Update();
        request.setTicketOpenAt(LocalDateTime.of(2025, 6, 15, 20, 30));

        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));

        // when
        PerformanceSchedule result = scheduleService.updateSchedule(id, request);

        // then
        verify(scheduleRepository).findById(id);
        assertThat(result.getTicketOpenAt()).isEqualTo(request.getTicketOpenAt());
        assertThat(result.getStartDatetime()).isEqualTo(START_DATETIME);
        assertThat(result.getTicketLimit()).isEqualTo(TICKET_LIMIT);
        assertThat(result.getStatus()).isEqualTo(UPCOMING);
    }

    @Test
    @DisplayName("모든 필드가 null인 경우 예외가 발생한다")
    public void update_allFieldsNull_throwsException() {
        // given
        Long id = 1L;
        Update request = new Update();

        // when & then
        assertThatThrownBy(() -> scheduleService.updateSchedule(id, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                            BusinessException ex = (BusinessException) e;
                            assertThat(ex.getErrorCode()).isEqualTo(PERFORMANCE_SCHEDULE_UPDATE_REQUEST_INVALID);
                        }
                );
    }

    @Test
    @DisplayName("공연 일정 수정 시 유효하지 않은 status 값이 들어오면 예외가 발생한다")
    public void update_invalidStatusValue_throwsException() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setStatus("INVALID");

        PerformanceSchedule schedule = new PerformanceSchedule(
                START_DATETIME, TICKET_OPEN_AT,
                TICKET_LIMIT, UPCOMING
        );

        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        when(scheduleMapper.toScheduleStatus(request.getStatus()))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertThatThrownBy(() -> scheduleService.updateSchedule(id, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                            BusinessException ex = (BusinessException) e;
                            assertThat(ex.getErrorCode()).isEqualTo(INVALID_ENUM_VALUE);
                        }
                );
        verify(scheduleRepository).findById(id);
        verify(scheduleMapper).toScheduleStatus(request.getStatus());
    }

    @Test
    @DisplayName("공연 일정 삭제 요청 시 해당 ID로 삭제가 수행된다")
    public void delete_success() {
        // given 
        Long id = 1L;

        // when
        scheduleService.deleteSchedule(id);

        // then
        verify(scheduleRepository).deleteById(id);
    }
}