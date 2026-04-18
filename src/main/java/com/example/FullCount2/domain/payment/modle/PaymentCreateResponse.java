package com.example.FullCount2.domain.payment.modle;

import com.example.FullCount2.common.enums.PaymentMethod;
import com.example.FullCount2.domain.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentCreateResponse {

    private final Long id; //결제 Id
    private final Long reservationId; //예매 Id
    private final int amount; // 결제 금액
    private final String status; //PENDING / COMPLETED / CANCELED
    private final PaymentMethod method; // 결제 방식 (BANK_TRANSFER)
    private LocalDateTime paidAt; // 입금 완료 시간 (PENDING = NULL 반환)

    public static PaymentCreateResponse from(Payment payment) {
        return PaymentCreateResponse.builder()
                .id(payment.getId())
                .reservationId(payment.getReservation().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
