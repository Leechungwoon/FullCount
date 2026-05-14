package com.example.FullCount2.domain.team.modle;

import com.example.FullCount2.domain.team.entity.Team;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTeamResponse {

    private final Long id; //팀 id
    private final String name; // 팀 이름(풀네임) -> ssg랜더스
    private final String shortName; // 팀 약칭 ssg
    private final String stadium; // 홈구장 이름

    public static GetTeamResponse from(Team team) {
        return GetTeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .shortName(team.getShortName())
                .stadium(team.getStadium().getName())
                .build();
    }
}
