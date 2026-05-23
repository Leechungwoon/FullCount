package com.example.FullCount2.domain.reservation.reposiroty;

import com.example.FullCount2.domain.reservation.entity.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    List<ReservationSeat> findByReservationId(Long reservationId);

    @Modifying
    @Query("""
            UPDATE GameSeat gs
            SET gs.status = 'AVAILABLE'
            WHERE gs.id IN(
            SELECT rs.gameSeat.id
            FROM ReservationSeat rs
            WHERE rs.reservation.status = 'HELD'
            AND rs.reservation.expiredAt < :now
            )
            """)
    int bulkRestoreExpiredGameSeats(@Param("now") LocalDateTime now);
}
