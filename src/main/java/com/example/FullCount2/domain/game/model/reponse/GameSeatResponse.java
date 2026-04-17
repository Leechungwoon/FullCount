package com.example.FullCount2.domain.game.model.reponse;

import com.example.FullCount2.domain.game.entity.GameSeat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameSeatResponse {

    private Long gameSeatId; //GameSeat ID (예매 시 id 요청)
    private int rowNum; //열 번호
    private int seatNum; //좌석 번호
    private String status; // 죄석 상태(AVAILABLE / HELD / SOLD)
    private int price; // 좌석 가격

    public static GameSeatResponse from(GameSeat gameSeat) {
        return GameSeatResponse.builder()
                .gameSeatId(gameSeat.getId())
                .rowNum(gameSeat.getSeat().getRowNum())
                .seatNum(gameSeat.getSeat().getSeatNum())
                .status(gameSeat.getStatus())
                .price(gameSeat.getSeat().getSection().getPrice())
                .build();
    }
}
