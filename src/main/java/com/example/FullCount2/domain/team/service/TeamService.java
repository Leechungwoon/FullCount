package com.example.FullCount2.domain.team.service;

import com.example.FullCount2.domain.team.modle.GetTeamResponse;
import com.example.FullCount2.domain.team.repository.TeamRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    //팀 목록 조회
    @Transactional(readOnly = true)
    public List<GetTeamResponse> getTeams() {
        return teamRepository.findAll()
                .stream()
                .map(GetTeamResponse::from)
                .toList();
    }
}
