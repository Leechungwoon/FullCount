package com.example.FullCount2.domain.payment.service;

import com.example.FullCount2.domain.payment.modle.TossConfirmRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TossPaymentsService {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Value("${toss.confirm-url}")
    private String confirmUrl;

    private final PaymentService paymentService;

    public void confirmTossPayment(String paymentKey, String orderId, Long amount) {

        // 토스 인증 헤더 생성
        String encoded = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());

        // 토스 승인 API 호출
        // paymentKey: 토스가 발급한 결제 고유 키
        // orderId: 주문 식별자
        // amount: 결제 금액 (위변조 검증용 - 요청 금액과 실제 금액 일치 여부를 토스가 검증)
        RestClient restClient = RestClient.create();

        TossConfirmRequest requestBody = new TossConfirmRequest(paymentKey, orderId, amount);

        restClient.post()
                .uri(confirmUrl)
                .header("Authorization", "Basic " + encoded)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();

        // 토스 승인 성공 -> 기존 confirmPayment 로직 실행
        // Payment PENDING → COMPLETED
        // Reservation HELD → CONFIRMED
        // GameSeat HELD → SOLD
        paymentService.confirmPaymentByOrderId(orderId);
    }
}
