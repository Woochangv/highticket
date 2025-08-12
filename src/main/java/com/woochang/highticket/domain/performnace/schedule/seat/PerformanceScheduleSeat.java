package com.woochang.highticket.domain.performnace.schedule.seat;

import com.woochang.highticket.domain.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "performance_schedule_seats")
public class PerformanceScheduleSeat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_code", length = 10, nullable = false)
    private String seatCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private SeatGrade grade;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SeatStatus status;

    public PerformanceScheduleSeat(String seatCode, SeatGrade grade, int price, SeatStatus status) {
        this.seatCode = seatCode;
        this.grade = grade;
        this.price = price;
        this.status = status;
    }

    public void updateWith(String seatCode, SeatGrade grade, int price, SeatStatus status) {
        this.seatCode = seatCode;
        this.grade = grade;
        this.price = price;
        this.status = status;
    }
}
