package com.example.FullCount2.domain.payment.entity;

import com.example.FullCount2.common.enums.PaymentMethod;
import com.example.FullCount2.common.global.BaseEntity;
import com.example.FullCount2.domain.reservation.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(unique = true, length = 200)
    private String paymentKey; // Toss 결제 고유키

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING / DONE / CANCELLED

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method = PaymentMethod.BANK_TRANSFER; //무통장 입금

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime paidAt;

    @Builder
    private Payment(Reservation reservation, int amount) {
        this.reservation = reservation;
        this.amount = amount;
    }

    public void complete(String paymentKey) {
        this.paymentKey = paymentKey;
        this.status = "DONE";
    }

    public void cancel() {
        this.status = "CANCELLED";
    }
}
