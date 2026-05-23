package com.example.FullCount2.domain.game.entity;

import com.example.FullCount2.common.enums.GameSeatStatus;
import com.example.FullCount2.common.global.BaseEntity;
import com.example.FullCount2.domain.seat.entity.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING) //DB에 문자열 저장
    private GameSeatStatus status = GameSeatStatus.AVAILABLE; // AVAILABLE / HELD / SOLD

    @Version // 낙관적 락 핵심
    private int version;

    @Builder
    private GameSeat(Game game, Seat seat) {
        this.game = game;
        this.seat = seat;
        this.status = GameSeatStatus.AVAILABLE; //기본값 명시적으로 설정
    }

    public void updateStatus(GameSeatStatus status) {
        this.status = status;
    }
}
