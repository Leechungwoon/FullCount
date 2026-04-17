package com.example.FullCount2.domain.reservation.model;

import com.example.FullCount2.common.enums.GameSeatStatus;
import com.example.FullCount2.domain.reservation.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationHeldResponse {

    private final Long id;
    private final String nickname;
    private final GameSeatStatus status;
    private final LocalDateTime reservedAt;
    private final LocalDateTime expiredAt;

    public static ReservationHeldResponse from(Reservation reservation) {
        return ReservationHeldResponse.builder()
                .id(reservation.getId())
                .nickname(reservation.getUser().getNickname())
                .status(GameSeatStatus.HELD)
                .reservedAt(reservation.getReservedAt())
                .expiredAt(reservation.getExpiredAt())
                .build();
    }
}
