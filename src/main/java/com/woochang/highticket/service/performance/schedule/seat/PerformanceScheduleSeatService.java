package com.woochang.highticket.service.performance.schedule.seat;

import com.woochang.highticket.domain.performnace.schedule.seat.PerformanceScheduleSeat;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatGrade;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatStatus;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.mapper.performance.schedule.seat.PerformanceScheduleSeatMapper;
import com.woochang.highticket.repository.performance.schedule.seat.PerformanceScheduleSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Create;
import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Update;
import static com.woochang.highticket.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceScheduleSeatService {

    private final PerformanceScheduleSeatRepository seatRepository;
    private final PerformanceScheduleSeatMapper seatMapper;

    @Transactional
    public PerformanceScheduleSeat createSeat(Create request) {
        PerformanceScheduleSeat seat = seatMapper.toEntity(request);
        return seatRepository.save(seat);
    }

    public PerformanceScheduleSeat getSeat(Long id) {
        return seatRepository.findById(id).orElseThrow(() ->
                new BusinessException(PERFORMANCE_SCHEDULE_SEAT_NOT_FOUND));
    }

    @Transactional
    public PerformanceScheduleSeat updateSeat(Long id, Update request) throws BusinessException {
        if (request.isAllFieldsNull()) {
            throw new BusinessException(PERFORMANCE_SCHEDULE_SEAT_UPDATE_REQUEST_INVALID);
        }

        if (request.getSeatCode() != null && request.getSeatCode().isBlank()) {
            throw new BusinessException(PERFORMANCE_SCHEDULE_SEAT_CODE_BLANK);
        }

        PerformanceScheduleSeat seat = getSeat(id);

        String seatCode = resolveValue(request.getSeatCode(), seat.getSeatCode());

        SeatGrade grade = request.getGrade() != null
                ? seatMapper.toSeatGrade(request.getGrade())
                : seat.getGrade();

        int price = resolveValue(request.getPrice(), seat.getPrice());

        SeatStatus status = request.getStatus() != null
                ? seatMapper.toSeatStatus(request.getStatus())
                : seat.getStatus();


        seat.updateWith(seatCode, grade, price, status);

        return seat;
    }

    @Transactional
    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    private <T> T resolveValue(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }
}
