package com.example.FullCount2.domain.reservation.service;

import com.example.FullCount2.domain.reservation.reposiroty.ReservationRepository;
import com.example.FullCount2.domain.reservation.reposiroty.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ReservationService reservationService;

    //    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0/30 * * * * *")
    @Transactional // 예매와 좌석 변경 한 번에 처리

    public void cancelExpiredReservation() {

        log.info("예매 만료 스케줄러 실행: {}", LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();

        int restoredSeats = reservationSeatRepository.bulkRestoreExpiredGameSeats(LocalDateTime.now());
        log.info("좌석 복구 완료: {}건", restoredSeats);

        //취소 처리
        int cancelledCount = reservationRepository.bulkCancelExpired(now);
        log.info("예매 취소 완료: {}건", cancelledCount);
    }
}
//변경 전 cancelExpiredReservation 코드
//    public void cancelExpiredReservation() {
//
//        log.info("예매 만료 스케줄러 실행: {}", LocalDateTime.now());
//
//        //만료된 HELD 예매 조회
//        List<Reservation> expiredReservations = reservationRepository.findByStatusAndExpiredAtBefore(ReservationStatus.HELD, LocalDateTime.now());
//
//        log.info("만료된 예매 수: {}", expiredReservations.size());
//
//        //취소 처리
//        for (Reservation reservation : expiredReservations) {
//            reservationService.cancelExpired(reservation.getId());
//
//            log.info("예매 취소 처리 완료 - reservationId: {}", reservation.getId());
//        }
//    }

