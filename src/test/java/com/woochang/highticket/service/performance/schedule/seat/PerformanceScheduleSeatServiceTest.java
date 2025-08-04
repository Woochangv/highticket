package com.woochang.highticket.service.performance.schedule.seat;

import com.woochang.highticket.domain.performnace.schedule.seat.PerformanceScheduleSeat;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatGrade;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatStatus;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.mapper.performance.schedule.seat.PerformanceScheduleSeatMapper;
import com.woochang.highticket.repository.performance.schedule.seat.PerformanceScheduleSeatRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Create;
import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Update;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    @DisplayName("공연 일정 좌석이 정상적으로 생성된다")
    public void create_success() {
        // given
        Create request = new Create();
        request.setSeatCode(SEAT_CODE);
        request.setGrade(GRADE);
        request.setPrice(PRICE);
        request.setStatus(STATUS);

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
        assertThatThrownBy(() -> seatService.getSeat(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PERFORMANCE_SCHEDULE_SEAT_NOT_FOUND.getMessage());
        verify(seatRepository).findById(id);
    }
    
    @Test
    @DisplayName("일부 필드(Grade)만 수정하면 나머지는 유지되고 정상적으로 변경된다")
    public void update_statusOnly_success() {
        // given
        Long id = 1L;
        Update request = new Update();
        request.setStatus("RESERVED");

        PerformanceScheduleSeat seat = new PerformanceScheduleSeat(SEAT_CODE, SeatGrade.VIP, PRICE, SeatStatus.AVAILABLE);

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
        assertThatThrownBy(() -> seatService.updateSeat(id, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PERFORMANCE_SCHEDULE_SEAT_UPDATE_REQUEST_INVALID.getMessage());
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
}