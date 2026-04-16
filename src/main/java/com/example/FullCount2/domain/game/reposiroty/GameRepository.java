package com.example.FullCount2.domain.game.reposiroty;

import com.example.FullCount2.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g " +
            "WHERE (:homeTeamId IS NULL OR g.homeTeam.id = :homeTeamId) " +
            "AND (:awayTeamId IS NULL OR g.awayTeam.id = :awayTeamId) " +
            "AND (:stadiumId IS NULL OR g.stadium.id = :stadiumId) " +
            "AND (:gameDate IS NULL OR CAST(g.gameDateTime AS date) = :gameDate) " +
            "ORDER BY g.gameDateTime ASC")
    List<Game> findByFilters(
            @Param("homeTeamId") Long homeTeamId, //홈팀 필터
            @Param("awayTeamId") Long awayTeamId, //어웨이팀 ID 필터
            @Param("stadiumId") Long stadiumId, //구장 ID 필터
            @Param("gameDate")LocalDate gameDate //경기 날짜 필터
            );
}
