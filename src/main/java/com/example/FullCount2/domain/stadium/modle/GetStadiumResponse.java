package com.example.FullCount2.domain.stadium.modle;

import com.example.FullCount2.domain.stadium.entity.Stadium;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStadiumResponse {

    private final Long id; //경기장 Id
    private final String name; // 경기장 이름
    private final String location; // 경기장 지역
    private final int totalCapacity; // 경기장 수용 인원

    public static GetStadiumResponse from(Stadium stadium) {
        return GetStadiumResponse.builder()
                .id(stadium.getId())
                .name(stadium.getName())
                .location(stadium.getLocation())
                .totalCapacity(stadium.getTotalCapacity())
                .build();
    }
}
