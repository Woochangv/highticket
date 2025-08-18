package com.woochang.highticket.service.performance.schedule;

import com.woochang.highticket.domain.performnace.schedule.PerformanceSchedule;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.mapper.performance.schedule.PerformanceScheduleMapper;
import com.woochang.highticket.repository.performance.schedule.PerformanceScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus.OPEN;
import static com.woochang.highticket.domain.performnace.schedule.PerformanceScheduleStatus.UPCOMING;
import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Create;
import static com.woochang.highticket.dto.performance.schedule.PerformanceScheduleDto.Update;
import static com.woochang.highticket.global.exception.ErrorCode.*;
import static com.woochang.highticket.support.BusinessExceptionAssertions.assertBusinessError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PerformanceScheduleServiceTest {

    private static final LocalDateTime START_DATETIME = LocalDateTime.of(2025, 7, 1, 18, 30);
    private static final LocalDateTime TICKET_OPEN_AT = LocalDateTime.of(2025, 6, 1, 20, 30);
    private static final int TICKET_LIMIT = 10000;
    private static final String STATUS = "UPCOMING";
    private static final LocalDateTime INVALID_START_DATETIME = LocalDateTime.of(2025, 5, 1, 18, 30);

    @InjectMocks
    private PerformanceScheduleService scheduleService;
    @Mock
    private PerformanceScheduleRepository scheduleRepository;
    @Mock
    private PerformanceScheduleMapper scheduleMapper;

    private static Stream<String> invalidValues() {
        return Stream.of("INVALID", null);
    }

    @Test
    @DisplayName("공연 일정이 정상적으로 생성된다")
    public void create_success() {
        // given
        Create request = baseCreate();

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
        Create request = baseCreate();
        request.setStartDatetime(INVALID_START_DATETIME);

        PerformanceSchedule schedule = new PerformanceSchedule(
                request.getStartDatetime(), request.getTicketOpenAt(),
                request.getTicketLimit(), UPCOMING
        );

        when(scheduleMapper.toEntity(request)).thenReturn(schedule);

        // when & then
        assertBusinessError(
                () -> scheduleService.createSchedule(request),
                PERFORMANCE_SCHEDULE_DATETIME_INVALID
        );
        verify(scheduleRepository, never()).save(any());
    }

    @ParameterizedTest(name = "[{index}] status={0} -> INVALID_ENUM_VALUE")
    @DisplayName("공연 일정 생성 시 유효하지 않은 status 값('INVALID', null)이 주어지면 예외가 발생한다")
    @MethodSource("invalidValues")
    public void create_rejects_invalidStatus(String status) {
        // given
        Create request = baseCreate();
        request.setStatus(status);

        when(scheduleMapper.toEntity(argThat(req -> Objects.equals(req.getStatus(), status))))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertBusinessError(
                () -> scheduleService.createSchedule(request),
                INVALID_ENUM_VALUE
        );
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하는 ID로 공연 일정 조회하면 공연 일정을 반환한다")
    public void getSchedule_success() {
        // given
        Long id = 1L;
        PerformanceSchedule schedule = baseSchedule();

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
        Long id = 999L;

        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertBusinessError(
                () -> scheduleService.getSchedule(id),
                PERFORMANCE_SCHEDULE_NOT_FOUND
        );
        verify(scheduleRepository).findById(id);
    }

    @Test
    @DisplayName("일부 필드(ticketOpenAt)만 수정하면 나머지는 유지되고 정상적으로 변경된다")
    public void update_ticketOpenAtOnly_success() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setTicketOpenAt(LocalDateTime.of(2025, 6, 15, 20, 30));

        PerformanceSchedule schedule = baseSchedule();

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
        assertBusinessError(
                () -> scheduleService.updateSchedule(id, request),
                PERFORMANCE_SCHEDULE_UPDATE_REQUEST_INVALID
        );
        verify(scheduleRepository, never()).findById(id);
    }

    @Test
    @DisplayName("공연 일정 수정 시 유효하지 않은 status 값('INVALID')이 주어지면 예외가 발생한다")
    public void update_rejects_invalidStatus() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setStatus("INVALID");

        PerformanceSchedule schedule = baseSchedule();

        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        when(scheduleMapper.toScheduleStatus("INVALID"))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertBusinessError(
                () -> scheduleService.updateSchedule(id, request),
                INVALID_ENUM_VALUE
        );
        verify(scheduleRepository).findById(id);
        verify(scheduleMapper).toScheduleStatus(request.getStatus());
    }

    @Test
    @DisplayName("공연 일정 수정 시 status가 null이면 매퍼 변환이 호출되지 않는다 ")
    public void update_nullStatus_doesNotCallMapper() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setTicketLimit(5000);
        request.setStatus(null);

        PerformanceSchedule schedule = baseSchedule();

        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));

        // when
        scheduleService.updateSchedule(id, request);

        // then
        verify(scheduleRepository).findById(id);
        verify(scheduleMapper, never()).toScheduleStatus(any());
    }

    @Test
    @DisplayName("공연 일정 수정 시 status가 주어지면 매퍼 변환이 한 번 호출 된다")
    public void update_status_callsMapperOnce() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setStatus("OPEN");

        PerformanceSchedule schedule = baseSchedule();

        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        when(scheduleMapper.toScheduleStatus("OPEN")).thenReturn(OPEN);

        // when
        scheduleService.updateSchedule(id, request);

        // then
        verify(scheduleRepository).findById(id);
        verify(scheduleMapper, times(1)).toScheduleStatus("OPEN");
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

    private Create baseCreate() {
        Create request = new Create();
        request.setStartDatetime(START_DATETIME);
        request.setTicketOpenAt(TICKET_OPEN_AT);
        request.setTicketLimit(TICKET_LIMIT);
        request.setStatus(STATUS);
        return request;
    }

    private PerformanceSchedule baseSchedule() {
        return new PerformanceSchedule(START_DATETIME, TICKET_OPEN_AT, TICKET_LIMIT, UPCOMING);
    }
}