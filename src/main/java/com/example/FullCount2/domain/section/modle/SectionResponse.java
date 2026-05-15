package com.example.FullCount2.domain.section.modle;

import com.example.FullCount2.domain.section.entity.Section;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SectionResponse {

    private final Long id;
    private final String name;
    private final String zoneType;
    private int price;

    public static SectionResponse from(Section section){
        return SectionResponse.builder()
                .id(section.getId())
                .name(section.getName())
                .zoneType(section.getZoneType())
                .price(section.getPrice())
                .build();
    }
}
