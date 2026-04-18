package com.example.FullCount2.domain.payment.service;

import com.example.FullCount2.common.enums.GameSeatStatus;
import com.example.FullCount2.common.enums.PaymentStatus;
import com.example.FullCount2.common.enums.ReservationStatus;
import com.example.FullCount2.domain.payment.entity.Payment;
import com.example.FullCount2.domain.payment.modle.PaymentCreateRequest;
import com.example.FullCount2.domain.payment.modle.PaymentCreateResponse;
import com.example.FullCount2.domain.payment.repository.PaymentRepository;
import com.example.FullCount2.domain.reservation.entity.Reservation;
import com.example.FullCount2.domain.reservation.reposiroty.ReservationRepository;
import com.example.FullCount2.domain.reservation.reposiroty.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    @Value("${payment.bank-name}")
    private String bankName;

    @Value("${payment.account-number}")
    private String accountNumber;

    @Value("${payment.account-holder}")
    private String accountHolder;

    //결제 생성
    //좌석 선점 시 무통장 결제를 생성 -> 입금 대기인 PENDING 상태이며 당일 23:59:59까지 입금가능
    // paidAt은 입금 확인 전까지 null
    @Transactional
    public PaymentCreateResponse createPayment(PaymentCreateRequest request) {

        //예매 조회
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        //HELD 상태인지 확인(선점된 예매만 결제 가능)
        if (!reservation.getStatus().equals(ReservationStatus.HELD)) {
            throw new IllegalArgumentException("선점된 상태에서만 결제 가능합니다.");
        }

        //결제 생성 (PENDING, PaidAt = null)
        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(request.getAmount())
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountHolder(accountHolder)
                .build();

        return PaymentCreateResponse.from(paymentRepository.save(payment));
    }

    //입금 확인(관리자용)
    @Transactional
    public void confirmPayment(Long paymentId) {

        //결제 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        //PENDING 상태인지 확인
        if (!payment.getStatus().equals(PaymentStatus.PENDING)) {
            throw new IllegalArgumentException("대기중인 결제만 확인할 수 있습니다.");
        }

        //결제 완료 처리
        payment.complete(payment.getPaymentKey());

        //예매 CONFIRMED 처리
        Reservation reservation = payment.getReservation();
        reservation.confirm();

        // 좌석 SOLD
        reservationSeatRepository.findByReservationId(reservation.getId())
                .forEach(rs -> rs.getGameSeat().updateStatus(GameSeatStatus.SOLD));
    }

    //결제 취소
    @Transactional
    public void cancelPayment(Long paymentId) {

        //결제 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        //PENDING 상태 확인
        if (payment.getStatus().equals(PaymentStatus.PENDING))
            throw new IllegalArgumentException("대기중인 결제만 취소할 수 있습니다.");

        //결제 취소
        payment.cancel();

        //예매취소
        Reservation reservation = payment.getReservation();
        reservation.cancel();

        //좌석 AVAILABLE 변경
        reservationSeatRepository.findByReservationId(reservation.getId())
                .forEach(rs -> rs.getGameSeat().updateStatus(GameSeatStatus.AVAILABLE));
    }
}
