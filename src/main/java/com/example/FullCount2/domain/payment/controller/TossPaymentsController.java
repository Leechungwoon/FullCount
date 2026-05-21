package com.example.FullCount2.domain.payment.controller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.payment.modle.TossConfirmRequest;
import com.example.FullCount2.domain.payment.service.TossPaymentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments/toss")
public class TossPaymentsController {

    private final TossPaymentsService tossPaymentsService;

    //토스 결제 승인 API
    @PostMapping("/confirm")
    public ResponseEntity<CommonResponse> confirmPaymentApi(
            @RequestBody TossConfirmRequest request) {

        tossPaymentsService.confirmTossPayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );

        return ResponseEntity.ok(CommonResponse.success("결제가 완료됐습니다."));
    }
}
