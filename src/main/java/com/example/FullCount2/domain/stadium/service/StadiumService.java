package com.example.FullCount2.domain.stadium.service;

import com.example.FullCount2.domain.stadium.entity.Stadium;
import com.example.FullCount2.domain.stadium.modle.GetStadiumResponse;
import com.example.FullCount2.domain.stadium.repository.StadiumRepository;
import com.example.FullCount2.domain.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class StadiumService {

    private StadiumRepository stadiumRepository;
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetStadiumResponse getStadium(Long stadiumId) {

        //경기장 조회
        Stadium foundStadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기장입니다."));

        //찾는 경기장 반환
        return GetStadiumResponse.from(foundStadium);
    }
}
