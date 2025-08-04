package com.woochang.highticket.service.performance.schedule.seat;

import com.woochang.highticket.domain.performnace.schedule.seat.PerformanceScheduleSeat;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatGrade;
import com.woochang.highticket.domain.performnace.schedule.seat.SeatStatus;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;
import com.woochang.highticket.mapper.performance.schedule.seat.PerformanceScheduleSeatMapper;
import com.woochang.highticket.repository.performance.schedule.seat.PerformanceScheduleSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Create;
import static com.woochang.highticket.dto.performance.schedule.seat.PerformanceScheduleSeatDto.Update;

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
                new BusinessException(ErrorCode.PERFORMANCE_SCHEDULE_SEAT_NOT_FOUND));
    }

    @Transactional
    public PerformanceScheduleSeat updateSeat(Long id, Update request) {
        if (request.isAllFieldsNull()) {
            throw new BusinessException(ErrorCode.PERFORMANCE_SCHEDULE_SEAT_UPDATE_REQUEST_INVALID);
        }

        PerformanceScheduleSeat seat = getSeat(id);

        String seatCode = seat.getSeatCode();
        SeatGrade grade = seat.getGrade();
        int price = seat.getPrice();
        SeatStatus status = seat.getStatus();

        if(request.getSeatCode() != null) seatCode = request.getSeatCode();
        if(request.getGrade() != null) grade = seatMapper.toSeatGrade(request.getGrade());
        if(request.getPrice() != null) price = request.getPrice();
        if(request.getStatus() != null) status = seatMapper.toSeatStatus(request.getStatus());

        seat.updateWith(seatCode, grade, price, status);

        return seat;
    }

    @Transactional
    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }
}
