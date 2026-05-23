package com.example.FullCount2.domain.payment.modle;

import lombok.Getter;

@Getter
public class PaymentCreateRequest {

    private Long reservationId; //결제할 예매 Id
    private int amount; // 결제 금액
}
