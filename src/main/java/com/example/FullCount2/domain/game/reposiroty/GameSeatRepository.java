package com.example.FullCount2.domain.game.reposiroty;

import com.example.FullCount2.domain.game.entity.GameSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameSeatRepository extends JpaRepository<GameSeat, Long> {

    List<GameSeat> findByGameIdAndSeatSectionId(Long gameId, Long sectionId);
}
