package com.example.FullCount2.domain.reservation.service;

import com.example.FullCount2.common.enums.GameSeatStatus;
import com.example.FullCount2.common.enums.ReservationStatus;
import com.example.FullCount2.domain.game.entity.GameSeat;
import com.example.FullCount2.domain.game.reposiroty.GameSeatRepository;
import com.example.FullCount2.domain.reservation.entity.Reservation;
import com.example.FullCount2.domain.reservation.entity.ReservationSeat;
import com.example.FullCount2.domain.reservation.model.ReservationHeldResponse;
import com.example.FullCount2.domain.reservation.reposiroty.ReservationRepository;
import com.example.FullCount2.domain.reservation.reposiroty.ReservationSeatRepository;
import com.example.FullCount2.domain.user.entity.User;
import com.example.FullCount2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service //Spring Bean으로 등록
@RequiredArgsConstructor //final 필드 생성자 자동 생성 의존성 주입
public class ReservationService {

    private final GameSeatRepository gameSeatRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    //예매 서비스
    //좌석 선택 및 선점 석택->HELD 변환
    @Transactional
    public ReservationHeldResponse reservationSelection(Long userId, Long gameSeatId) {

        //회원 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        //좌석 조회
        GameSeat gameSeat = gameSeatRepository.findById(gameSeatId)
                .orElseThrow(() -> new IllegalArgumentException("죄석 정보가 없습니다."));

        //선점된 좌석인지 확인
        if (!gameSeat.getStatus().equals(GameSeatStatus.AVAILABLE)) {
            throw new IllegalArgumentException("이미 선정된 좌석입니다.");
        }

        //좌석 상태를 HELD 변경
        gameSeat.updateStatus(GameSeatStatus.HELD);

        LocalDateTime expiredAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));

        //임시 예매 생성
        Reservation reservation = Reservation.builder()
                .user(user)
                .expiredAt(expiredAt)
                .build();

        reservationRepository.save(reservation);

        //예매 좌석 중간 테이블 저장
        ReservationSeat reservationSeat = ReservationSeat.builder()
                .reservation(reservation)
                .gameSeat(gameSeat)
                .build();

        reservationSeatRepository.save(reservationSeat);

        return ReservationHeldResponse.from(reservation);
    }

    // 예매 취소
    @Transactional
    public void cancel(Long userId, Long reservationId) {

        //예매 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        //본인 예매 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        //이미 취소된 예매인지 확인
        if (reservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            throw new IllegalArgumentException("이미 취소된 예매입니다.");
        }

        //예매 상태 변경
        reservation.cancel();

        //좌석 상태 변경
        reservationSeatRepository.findByReservationId(reservationId)
                .forEach(rs -> rs.getGameSeat().updateStatus(GameSeatStatus.AVAILABLE));
    }

    //스케줄러 전용 예매 취소(자정에 초기화)
    @Transactional
    public void cancelExpired(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        //예매 CANCEL 상태 변경
        reservation.cancel();

        //CANCEL로 예매 자리 AVAILABLE로 변경
        reservationSeatRepository.findByReservationId(reservation.getId())
                .forEach(rs -> rs.getGameSeat().updateStatus(GameSeatStatus.AVAILABLE));
    }
}
