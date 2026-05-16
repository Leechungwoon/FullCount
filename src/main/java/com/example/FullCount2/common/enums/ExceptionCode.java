package com.example.FullCount2.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    //회원


    // 팀

    // 경기장

    // 경기장 구역

    // 좌석

    // 예매

    // 결제

    // 게임

    // 인증인가

    ;
    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
