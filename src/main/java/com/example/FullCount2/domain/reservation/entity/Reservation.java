package com.example.FullCount2.domain.reservation.entity;

import com.example.FullCount2.common.global.BaseEntity;
import com.example.FullCount2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String status = "HELD"; // HELD / CONFIRMED / CANCELLED

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    private LocalDateTime expiredAt; // HELD 만료 시각 (5분)

    @Builder
    private Reservation(User user, LocalDateTime expiredAt) {
        this.user = user;
        this.reservedAt = LocalDateTime.now();
        this.expiredAt = expiredAt;
    }

    public void confirm() {
        this.status = "CONFIRMED";
        this.expiredAt = null;
    }

    public void cancel() {
        this.status = "CANCELLED";
    }
}
