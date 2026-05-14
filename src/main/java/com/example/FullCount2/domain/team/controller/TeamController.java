package com.example.FullCount2.domain.team.controller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.game.model.reponse.GameResponse;
import com.example.FullCount2.domain.game.service.GameService;
import com.example.FullCount2.domain.team.modle.GetTeamResponse;
import com.example.FullCount2.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/teams")
@RestController
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    private final GameService gameService;

    //홈 팀 경기 목록 조회
    @GetMapping("/{teamId}/games")
    public ResponseEntity<CommonResponse> getHomeTeamGamesListApi(@PathVariable Long teamId) {

        //비지니스 로직
        List<GameResponse> responses = gameService.getHomeTeamGameList(teamId);

        //Dto 반환
        return ResponseEntity.ok(CommonResponse.success("홈팀 경기 일정이 조회됐습니다.", responses));
    }

    //팀 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse> getTeamsApi() {

        //비지니스 로직
        List<GetTeamResponse> responses = teamService.getTeams();

        //Dto 반환
        return ResponseEntity.ok(CommonResponse.success("팀 목록이 조회됐습니다.", responses));
    }
}
