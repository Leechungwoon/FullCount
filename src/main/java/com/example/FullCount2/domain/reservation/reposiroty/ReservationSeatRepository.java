package com.example.FullCount2.domain.reservation.reposiroty;

import com.example.FullCount2.domain.reservation.entity.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
}
