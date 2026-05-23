package com.example.FullCount2.domain.seat.modle;

import com.example.FullCount2.domain.seat.entity.Seat;
import com.example.FullCount2.domain.section.entity.Section;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetSeatResponse {

    private final Long id;
    private final int rowNum;
    private final int seatNum;

    public static GetSeatResponse from(Seat seat) {
        return GetSeatResponse.builder()
                .id(seat.getId())
                .rowNum(seat.getRowNum())
                .seatNum(seat.getSeatNum())
                .build();
    }
}
