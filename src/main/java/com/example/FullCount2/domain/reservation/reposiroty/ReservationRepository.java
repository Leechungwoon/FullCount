package com.example.FullCount2.domain.reservation.reposiroty;

import com.example.FullCount2.common.enums.ReservationStatus;
import com.example.FullCount2.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatusAndExpiredAtBefore(ReservationStatus status, LocalDateTime now);

    @Modifying
    @Query("""
            UPDATE Reservation r
            SET r.status = 'CANCELLED'
            WHERE r.status = 'HELD'
            AND r.expiredAt < :now
            """)
    int bulkCancelExpired(@Param("now") LocalDateTime now);
}
