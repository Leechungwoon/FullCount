package com.example.FullCount2.domain.team.service;

import com.example.FullCount2.domain.team.repository.TeamRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;


}
