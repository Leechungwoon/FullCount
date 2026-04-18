package com.example.FullCount2.domain.payment.service;

import com.example.FullCount2.domain.payment.entity.Payment;
import com.example.FullCount2.domain.payment.modle.PaymentCreateRequest;
import com.example.FullCount2.domain.payment.modle.PaymentCreateResponse;
import com.example.FullCount2.domain.payment.repository.PaymentRepository;
import com.example.FullCount2.domain.reservation.entity.Reservation;
import com.example.FullCount2.domain.reservation.reposiroty.ReservationRepository;
import com.example.FullCount2.domain.reservation.reposiroty.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    //결제 생성
    //좌석 선점 시 무통장 결제를 생성 -> 입금 대기인 PENDING 상태이며 당일 23:59:59까지 입금가능
    // paidAt은 입금 확인 전까지 null
    @Transactional
    public PaymentCreateResponse createPayment(PaymentCreateRequest request) {

        //예매 조회
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        //HELD 상태인지 확인(선점된 예매만 결제 가능)
        if (!reservation.getStatus().equals("HELD")) {
            throw new IllegalArgumentException("선점된 상태에서만 결제 가능합니다.");
        }

        //결제 생성 (PENDING, PaidAt = null)
        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(request.getAmount())
                .build();

        return PaymentCreateResponse.from(paymentRepository.save(payment));
    }


}
