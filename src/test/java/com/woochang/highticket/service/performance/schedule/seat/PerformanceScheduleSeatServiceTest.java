package com.woochang.highticket.service.performance.schedule.seat;

import com.woochang.highticket.domain.performnace.schedule.seat.PerformanceScheduleSeat;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatGrade;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatStatus;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.mapper.performance.schedule.seat.PerformanceScheduleSeatMapper;
import com.woochang.highticket.repository.performance.schedule.seat.PerformanceScheduleSeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Create;
import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Update;
import static com.woochang.highticket.global.exception.ErrorCode.*;
import static com.woochang.highticket.support.BusinessExceptionAssertions.assertBusinessError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceScheduleSeatServiceTest {

    private static final String SEAT_CODE = "A-01-01";
    private static final String GRADE = "VIP";
    private static final int PRICE = 139000;
    private static final String STATUS = "AVAILABLE";

    @InjectMocks
    PerformanceScheduleSeatService seatService;
    @Mock
    private PerformanceScheduleSeatRepository seatRepository;
    @Mock
    private PerformanceScheduleSeatMapper seatMapper;

    private static Stream<String> invalidValues() {
        return Stream.of("INVALID", null);
    }

    @Test
    @DisplayName("공연 일정 좌석이 정상적으로 생성된다")
    public void create_success() {
        // given
        Create request = baseCreate();

        PerformanceScheduleSeat seat = new PerformanceScheduleSeat(
                request.getSeatCode(), SeatGrade.VIP,
                request.getPrice(), SeatStatus.AVAILABLE);

        when(seatMapper.toEntity(request)).thenReturn(seat);
        when(seatRepository.save(seat)).thenReturn(seat);

        // when
        PerformanceScheduleSeat result = seatService.createSeat(request);

        // then
        verify(seatMapper).toEntity(request);
        verify(seatRepository).save(seat);
        assertThat(result.getSeatCode()).isEqualTo(request.getSeatCode());
        assertThat(result.getGrade()).isEqualTo(SeatGrade.VIP);
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
        assertThat(result.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @ParameterizedTest(name = "[{index}] grade={0} -> INVALID_ENUM_VALUE")
    @DisplayName("공연 일정 좌석 생성 시 유효하지 않은 grade 값('INVALID', null)이 주어지면 예외가 발생한다")
    @MethodSource("invalidValues")
    public void create_rejects_invalidGrade(String grade) {
        // given
        Create request = baseCreate();
        request.setGrade(grade);

        when(seatMapper.toEntity(argThat(req -> Objects.equals(req.getGrade(), grade))))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertBusinessError(
                () -> seatService.createSeat(request),
                INVALID_ENUM_VALUE
        );
        verify(seatRepository, never()).save(any());
    }

    @ParameterizedTest(name = "[{index}] status={0} -> INVALID_ENUM_VALUE")
    @DisplayName("공연 일정 좌석 생성 시 유효하지 않은 status 값('INVALID', null)이 주어지면 예외가 발생한다")
    @MethodSource("invalidValues")
    public void create_rejects_invalidStatus(String status) {
        // given
        Create request = baseCreate();
        request.setStatus(status);

        when(seatMapper.toEntity(argThat(req -> Objects.equals(req.getStatus(), status))))
                .thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertBusinessError(
                () -> seatService.createSeat(request),
                INVALID_ENUM_VALUE
        );
        verify(seatRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하는 ID로 조회 시 해당 공연 일정 좌석을 반환한다")
    public void getSeat_success() {
        // given
        Long id = 1L;

        PerformanceScheduleSeat seat = new PerformanceScheduleSeat(
                SEAT_CODE, SeatGrade.VIP,
                PRICE, SeatStatus.AVAILABLE);

        when(seatRepository.findById(id)).thenReturn(Optional.of(seat));

        // when
        PerformanceScheduleSeat result = seatService.getSeat(id);

        // then
        verify(seatRepository).findById(id);
        assertThat(result.getSeatCode()).isEqualTo(SEAT_CODE);
        assertThat(result.getGrade()).isEqualTo(SeatGrade.VIP);
        assertThat(result.getPrice()).isEqualTo(PRICE);
        assertThat(result.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
    public void getSeat_notFound_throwsException() {
        // given
        Long id = 999L;

        when(seatRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertBusinessError(
                () -> seatService.getSeat(id),
                PERFORMANCE_SCHEDULE_SEAT_NOT_FOUND
        );
        verify(seatRepository).findById(id);
    }

    @Test
    @DisplayName("일부 필드(status)만 수정하면 나머지는 유지되고 정상적으로 변경된다")
    public void update_statusOnly_success() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setStatus("RESERVED");

        PerformanceScheduleSeat seat = baseSeat();

        when(seatRepository.findById(id)).thenReturn(Optional.of(seat));
        when(seatMapper.toSeatStatus("RESERVED")).thenReturn(SeatStatus.RESERVED);

        // when
        PerformanceScheduleSeat result = seatService.updateSeat(id, request);

        // then
        assertThat(result.getSeatCode()).isEqualTo(SEAT_CODE);
        assertThat(result.getGrade()).isEqualTo(SeatGrade.VIP);
        assertThat(result.getPrice()).isEqualTo(PRICE);
        assertThat(result.getStatus()).isEqualTo(SeatStatus.RESERVED);
    }

    @Test
    @DisplayName("모든 필드가 null인 경우 예외가 발생한다")
    public void update_allFieldsNull_throwsException() {
        // given
        Long id = 1L;
        Update request = new Update();

        // when & then
        assertBusinessError(
                () -> seatService.updateSeat(id, request),
                PERFORMANCE_SCHEDULE_SEAT_UPDATE_REQUEST_INVALID
        );
        verify(seatRepository, never()).findById(any());
    }

    @Test
    @DisplayName("공연 일정 좌석 수정 시 status가 null이면 Mapper 변환이 호출되지 않는다")
    public void update_nullStatus_doesNotCallMapper() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setSeatCode(SEAT_CODE);
        request.setStatus(null);

        PerformanceScheduleSeat seat = baseSeat();

        when(seatRepository.findById(id)).thenReturn(Optional.of(seat));

        // when
        seatService.updateSeat(id, request);

        // then
        verify(seatRepository).findById(id);
        verify(seatMapper, never()).toSeatStatus(anyString());
    }

    @Test
    @DisplayName("공연 일정 좌석 수정 시 status가 주어지면 매퍼 변환이 한 번 호출된다")
    public void update_status_callsMapperOnce() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setStatus("RESERVED");

        PerformanceScheduleSeat seat = baseSeat();

        when(seatRepository.findById(id)).thenReturn(Optional.of(seat));
        when(seatMapper.toSeatStatus("RESERVED")).thenReturn(SeatStatus.RESERVED);

        // when
        seatService.updateSeat(id, request);

        // then
        verify(seatRepository).findById(id);
        verify(seatMapper, times(1)).toSeatStatus("RESERVED");
    }

    @Test
    @DisplayName("공연 일정 좌석 수정 시 유효하지 않은 status 값('INVALID')이 주어지면 예외가 발생한다")
    public void update_rejects_invalidStatus() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setStatus("INVALID");

        PerformanceScheduleSeat seat = baseSeat();

        when(seatRepository.findById(id)).thenReturn(Optional.of(seat));
        when(seatMapper.toSeatStatus("INVALID")).thenThrow(new BusinessException(INVALID_ENUM_VALUE));

        // when & then
        assertBusinessError(
                () -> seatService.updateSeat(id, request),
                INVALID_ENUM_VALUE
        );
    }

    @Test
    @DisplayName("공연 일정 좌석 수정 시 좌석 코드가 공백이면 예외가 발생한다")
    public void update_blankSeatCode_throwsException() {
        // given 
        Long id = 1L;
        Update request = new Update();
        request.setSeatCode(" ");

        // when & then
        assertBusinessError(
                () -> seatService.updateSeat(id,request),
                PERFORMANCE_SCHEDULE_SEAT_CODE_BLANK
        );
    }

    @Test
    @DisplayName("공연 일정 좌석 삭제 요청 시 해당 ID로 삭제가 수행된다")
    public void delete_success() {
        // given
        Long id = 1L;

        // when
        seatService.deleteSeat(id);

        // then
        verify(seatRepository).deleteById(id);
    }

    private Create baseCreate() {
        Create request = new Create();
        request.setSeatCode(SEAT_CODE);
        request.setGrade(GRADE);
        request.setPrice(PRICE);
        request.setStatus(STATUS);
        return request;
    }

    private PerformanceScheduleSeat baseSeat() {
        return new PerformanceScheduleSeat(SEAT_CODE, SeatGrade.VIP, PRICE, SeatStatus.AVAILABLE);
    }
}