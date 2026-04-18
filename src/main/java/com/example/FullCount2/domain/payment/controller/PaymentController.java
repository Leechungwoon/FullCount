package com.example.FullCount2.domain.payment.controller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.payment.modle.PaymentCreateRequest;
import com.example.FullCount2.domain.payment.modle.PaymentCreateResponse;
import com.example.FullCount2.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 생성 API
    @PostMapping
    public ResponseEntity<CommonResponse> paymentApi(@RequestBody PaymentCreateRequest request) {

        //핵심 비지니스
        PaymentCreateResponse response = paymentService.createPayment(request);

        //응답 반환
        return ResponseEntity.ok(CommonResponse.success("무통장이 생성됐습니다.", response));
    }

    //입금 확인(관리자 전용)
    @PatchMapping("{paymentId}/confirm")
    public ResponseEntity<CommonResponse> confirmPaymentApi(@PathVariable Long paymentId) {

        //핵심 비지니스
        paymentService.confirmPayment(paymentId);

        //응답 반환
        return ResponseEntity.ok(CommonResponse.success("입금이 확인됐습니다."));
    }

    //입금 취소
    @DeleteMapping("/{paymentId}/cancel")
    public ResponseEntity<CommonResponse> cancelPaymentApi(@PathVariable Long paymentId) {

        //핵심 비지니스
        paymentService.cancelPayment(paymentId);

        //응답 반환
        return ResponseEntity.ok(CommonResponse.success("입금이 취소됐습니다."));
    }
}
