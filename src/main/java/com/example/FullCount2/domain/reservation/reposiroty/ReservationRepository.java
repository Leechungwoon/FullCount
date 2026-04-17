package com.example.FullCount2.domain.reservation.reposiroty;

import com.example.FullCount2.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
