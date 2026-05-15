package com.example.FullCount2.domain.seat.controller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.seat.modle.GetSeatResponse;
import com.example.FullCount2.domain.seat.service.SeatService;
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
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/{sectionId}/seats")
    public ResponseEntity<CommonResponse> getSeatApi(@PathVariable Long sectionId) {

        //핵심 비지니스
        List<GetSeatResponse> responses = seatService.getSeat(sectionId);

        //Dto 반환
        return ResponseEntity.ok(CommonResponse.success("좌석이 조회됐습니다.", responses));
    }
}
