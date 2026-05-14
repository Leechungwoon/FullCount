package com.example.FullCount2.domain.stadium.controller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.stadium.modle.GetStadiumResponse;
import com.example.FullCount2.domain.stadium.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/stadiums")
@RestController
@RequiredArgsConstructor
public class StadiumController {
    private final StadiumService stadiumService;

    // 경기장 단 건 조회
    @GetMapping("/detail/{stadiumId}")
    public ResponseEntity<CommonResponse> stadiumDetailGetApi(@PathVariable Long stadiumId) {

        //비지니스 로직
        GetStadiumResponse response = stadiumService.getStadium(stadiumId);

        //Dto 반환
        return ResponseEntity.ok(CommonResponse.success("경기장 조회됐습니다.", response));
    }

    // 경기장 목록 조회
    @GetMapping("/list")
    public ResponseEntity<CommonResponse> getStadiumListApi() {

        //비지니스 로직
        List<GetStadiumResponse> responses = stadiumService.getStadiumList();

        // Dto 반환
        return ResponseEntity.ok(CommonResponse.success("경기장 목록이 조회됐습니다.", responses));
    }
}
