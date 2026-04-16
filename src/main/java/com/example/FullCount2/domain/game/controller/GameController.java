package com.example.FullCount2.domain.game.controller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.game.model.reponse.GameResponse;
import com.example.FullCount2.domain.game.model.reponse.GameSeatResponse;
import com.example.FullCount2.domain.game.service.GameService;
import lombok.AllArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/games")
@AllArgsConstructor
public class GameController {

    private final GameService gameService;

    // 경기 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse> getGameListApi(
            @RequestParam(required = false) Long homeTeamId,
            @RequestParam(required = false) Long awayTeamId,
            @RequestParam(required = false) Long stadiumId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate gameDateTime
    ) {
        List<GameResponse> responseList = gameService.getGames(homeTeamId, awayTeamId, stadiumId, gameDateTime)
                .stream()
                .map(GameResponse::from)
                .toList();

        return ResponseEntity.ok(CommonResponse.success("경기 목록이 조회 됐습니다.", responseList));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<CommonResponse> getGameApi(@PathVariable Long gameId) {

        GameResponse response = GameResponse.from(gameService.getGame(gameId));

        return ResponseEntity.ok(CommonResponse.success("경기 조회 됐습니다.", response));
    }

    //구역별 좌석 상태 조회
    @GetMapping("/{gameId}/sections/{sectionId}/seats")
    public ResponseEntity<CommonResponse> gameSeatApi(@PathVariable Long gameId, @PathVariable Long sectionId) {

        List<GameSeatResponse> responses = gameService.getGameStats(gameId, sectionId)
                .stream()
                .map(GameSeatResponse::from)
                .toList();

        return ResponseEntity.ok(CommonResponse.success("좌석 조회가 완료 됐습니다.", responses));
    }
}
