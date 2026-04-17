package com.example.FullCount2.domain.reservation.controller;

import com.example.FullCount2.common.global.CommonResponse;
import com.example.FullCount2.domain.reservation.model.ReservationHeldResponse;
import com.example.FullCount2.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/reservation")
@RestController

public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/held")
    public ResponseEntity<CommonResponse> reservationSelectionApi(@AuthenticationPrincipal Long userId, @RequestParam Long gameSeatId) {

        //핵심 비지니스
        ReservationHeldResponse response = reservationService.reservationSelection(userId,gameSeatId);

        //응답 반환
        return ResponseEntity.ok(CommonResponse.success("예매 선정됐습니다.",response));
    }
}
