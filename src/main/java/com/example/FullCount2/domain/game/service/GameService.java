package com.example.FullCount2.domain.game.service;

import com.example.FullCount2.domain.game.entity.Game;
import com.example.FullCount2.domain.game.entity.GameSeat;
import com.example.FullCount2.domain.game.reposiroty.GameRepository;
import com.example.FullCount2.domain.game.reposiroty.GameSeatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameSeatRepository gameSeatRepository;

    @Transactional(readOnly = true)
    public List<Game> getGames(Long homeTeamId, Long awayTeamId, Long stadiumId, LocalDate gameDate) {
        return gameRepository.findByFilters(homeTeamId, awayTeamId, stadiumId, gameDate);
    }

    //경기 단건 조회
    @Transactional(readOnly = true)
    public Game getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기입니다."));
    }

    //경기장 구역별 죄석 상태 조회
    @Transactional(readOnly = true)
    public List<GameSeat> getGameStats(Long gameId, Long sectionId) {
        return gameSeatRepository.findByGameIdAndSeatSectionId(gameId, sectionId);
    }
}
