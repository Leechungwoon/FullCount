package com.example.FullCount2.domain.section.entity;

import com.example.FullCount2.common.global.BaseEntity;
import com.example.FullCount2.domain.stadium.entity.Stadium;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sections")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String zoneType;

    @Column(nullable = false)
    private int price;

    @Builder
    private Section(Stadium stadium, String name, String zoneType, int price) {
        this.stadium = stadium;
        this.name = name;
        this.zoneType = zoneType;
        this.price = price;
    }
}
