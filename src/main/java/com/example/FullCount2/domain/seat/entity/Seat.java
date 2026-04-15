package com.example.FullCount2.domain.seat.entity;

import com.example.FullCount2.domain.section.entity.Section;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private int rowNum;

    @Column(nullable = false)
    private int seatNum;

    @Builder
    private Seat(Section section, int rowNum, int seatNum) {
        this.section = section;
        this.rowNum = rowNum;
        this.seatNum = seatNum;
    }
}
