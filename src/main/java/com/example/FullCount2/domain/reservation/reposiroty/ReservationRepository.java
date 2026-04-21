package com.example.FullCount2.domain.reservation.reposiroty;

import com.example.FullCount2.common.enums.ReservationStatus;
import com.example.FullCount2.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatusAndExpiredAtBefore(ReservationStatus status, LocalDateTime now);
}
