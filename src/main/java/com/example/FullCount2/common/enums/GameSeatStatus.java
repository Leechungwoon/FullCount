package com.example.FullCount2.common.enums;

import lombok.Getter;

@Getter
public enum GameSeatStatus {

    AVAILABLE, //예매 가능
    HELD, //선점됨(결제 대기중)
    SOLD, //결제 완료
}
