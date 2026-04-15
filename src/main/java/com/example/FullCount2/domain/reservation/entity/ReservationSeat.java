package com.example.FullCount2.domain.reservation.entity;

import com.example.FullCount2.domain.game.entity.GameSeat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation_seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_seat_id", nullable = false)
    private GameSeat gameSeat;

    @Builder
    private ReservationSeat(Reservation reservation, GameSeat gameSeat) {
        this.reservation = reservation;
        this.gameSeat = gameSeat;
    }
}
