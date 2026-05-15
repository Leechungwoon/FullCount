package com.example.FullCount2.domain.section.service;

import com.example.FullCount2.domain.game.entity.Game;
import com.example.FullCount2.domain.game.reposiroty.GameRepository;
import com.example.FullCount2.domain.section.modle.SectionResponse;
import com.example.FullCount2.domain.section.reposiroty.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final GameRepository gameRepository;

    @Transactional(readOnly = true)
    public List<SectionResponse> getSection(Long gameId) {
        //경기 조회
        Game foundGame = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기입니다."));

        //경기 구장 목록 조회
        Long stadiumId = foundGame.getStadium().getId();

        return sectionRepository.findByStadiumId(stadiumId)
                .stream()
                .map(SectionResponse::from)
                .toList();
    }
}
