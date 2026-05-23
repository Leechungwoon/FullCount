package com.example.FullCount2.domain.game.model.reponse;

import com.example.FullCount2.domain.game.entity.Game;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GameResponse {

    private Long id;
    private String homeTeam; //홈팀 이름
    private String awayTeam; //어웨이팀 이름
    private String stadium; //경기장 이름
    private LocalDateTime gameDateTime; //경기 시간
    private String status; //상태(SCHEDULED / OPEN / CLOSED / CANCELLED)


    public static GameResponse from(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .homeTeam(game.getHomeTeam().getName())
                .awayTeam(game.getAwayTeam().getName())
                .stadium(game.getStadium().getName())
                .gameDateTime(game.getGameDateTime())
                .status(game.getStatus())
                .build();
    }
}
