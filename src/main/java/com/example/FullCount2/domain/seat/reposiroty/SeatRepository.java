package com.example.FullCount2.domain.seat.reposiroty;

import com.example.FullCount2.domain.seat.entity.Seat;
import com.example.FullCount2.domain.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findBySectionId(Long sectionId);
}
