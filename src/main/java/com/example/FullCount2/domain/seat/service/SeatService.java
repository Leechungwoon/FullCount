package com.example.FullCount2.domain.seat.service;

import com.example.FullCount2.domain.seat.modle.GetSeatResponse;
import com.example.FullCount2.domain.seat.reposiroty.SeatRepository;
import com.example.FullCount2.domain.section.entity.Section;
import com.example.FullCount2.domain.section.reposiroty.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final SectionRepository sectionRepository;

    @Transactional(readOnly = true)
    public List<GetSeatResponse> getSeat(Long sectionId) {

        // 경기장 구역 확인
        Section foundSection = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기 좌석입니다."));

        // 좌석 목록 조회
        return seatRepository.findBySectionId(sectionId)
                .stream()
                .map(GetSeatResponse::from)
                .toList();
    }
}
