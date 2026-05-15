package com.example.FullCount2.domain.section.controller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.section.modle.SectionResponse;
import com.example.FullCount2.domain.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/sections")
@RestController
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    //좌석 구역 조회
    @GetMapping("/games/{gameId}")
    public ResponseEntity<CommonResponse> getSectionApi(@PathVariable Long gameId) {

        //핵심 비지니스
        List<SectionResponse> responses = sectionService.getSection(gameId);

        //Dto 반환
        return ResponseEntity.ok(CommonResponse.success("반환됐습니다.", responses));
    }
}
